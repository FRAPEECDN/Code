package foundation.io.network;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import org.java_websocket.WebSocket;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.handshake.ServerHandshake;
import org.java_websocket.server.WebSocketServer;

/**
 * Starts local echo servers and demonstrates a client exchange for TCP, UDP,
 * HTTP/1.1, and WebSocket.
 *
 * <p>
 * Each protocol example creates its server first, chooses an
 * operating-system-assigned port, and then creates a
 * client that connects to that address. The server returns the request payload
 * as the response so the client can
 * observe the complete round trip. All listeners bind to loopback and are
 * stopped before the method returns.
 *
 * <ul>
 * <li><b>TCP</b> supplies a reliable, ordered byte stream. It does not preserve
 * application message boundaries, so
 * this example defines a newline as the end of one text request.</li>
 * <li><b>UDP</b> sends independent datagrams. A datagram has a boundary and
 * source address, but delivery and order
 * are not guaranteed and there is no connection handshake.</li>
 * <li><b>HTTP/1.1</b> defines a request/response exchange over TCP. The client
 * sends a method, path, headers, and body;
 * the server responds with a status, headers, and body, then the exchange
 * ends.</li>
 * <li><b>WebSocket</b> begins with an HTTP upgrade handshake and then keeps a
 * connection open for framed messages in
 * either direction.</li>
 * </ul>
 *
 * <p>
 * The examples use blocking sockets with short timeouts to keep the flow
 * visible. Java 21 virtual threads and
 * Java 25 structured concurrency are demonstrated in their own source sets
 * rather than hidden in these baseline
 * protocol examples.
 */
public final class NetworkExamples {
    private static final InetAddress LOOPBACK = InetAddress.getLoopbackAddress();
    private static final int TIMEOUT_SECONDS = 5;

    private NetworkExamples() {
    }

    /**
     * Runs the same request/echo-response pattern through each protocol
     * implementation.
     *
     * <p>
     * This method is sequential: it completes TCP, then UDP, HTTP/1.1, and
     * WebSocket. Each private method owns
     * the corresponding server, client, worker, and cleanup for one exchange.
     *
     * @return the response received by each protocol's client
     * @throws IOException          if a socket or HTTP operation fails
     * @throws InterruptedException if an exchange is interrupted
     * @throws ExecutionException   if a server task fails
     * @throws TimeoutException     if a local server does not respond in time
     */
    public static Report run() throws IOException, InterruptedException, ExecutionException, TimeoutException {
        return new Report(
                tcpEcho("tcp request"),
                udpEcho("udp request"),
                httpEcho("http/1.1 request"),
                webSocketEcho("websocket request"));
    }

    /**
     * Demonstrates a TCP server accepting a connection and echoing one
     * newline-delimited request.
     *
     * <p>
     * The server binds a {@link ServerSocket} to loopback and an ephemeral port.
     * Its worker calls
     * {@link ServerSocket#accept()}, which blocks until the client creates a
     * {@link Socket} to that port. The client
     * writes UTF-8 text followed by a newline and flushes; the newline is this
     * example's message framing because TCP
     * itself only provides a byte stream. The server's
     * {@link BufferedReader#readLine()} waits for that delimiter,
     * then the writer sends the same line and a newline back. The client reads one
     * response line.
     *
     * @param message text request sent by the client
     * @return text line received as the response
     * @throws IOException          if binding, connecting, or reading/writing fails
     * @throws InterruptedException if waiting for the server worker is interrupted
     * @throws ExecutionException   if the server worker fails
     * @throws TimeoutException     if the server worker does not finish before its
     *                              deadline
     */
    private static String tcpEcho(String message)
            throws IOException, InterruptedException, ExecutionException, TimeoutException {
        // Binding port 0 asks the operating system for a free port. accept() runs
        // separately so this
        // method can also act as the client without blocking before it connects.
        ExecutorService serverExecutor = Executors.newSingleThreadExecutor();
        try (ServerSocket server = new ServerSocket(0, 1, LOOPBACK)) {
            server.setSoTimeout(Math.toIntExact(TimeUnit.SECONDS.toMillis(TIMEOUT_SECONDS)));
            Future<String> serverResponse = serverExecutor.submit(() -> {
                // accept() returns only after the client completes the TCP connection
                // handshake.
                try (Socket socket = server.accept();
                        BufferedReader reader = new BufferedReader(
                                new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
                        BufferedWriter writer = new BufferedWriter(
                                new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8))) {
                    String request = reader.readLine();
                    // The newline, rather than one read() call, marks the end of this application
                    // message.
                    writer.write(request);
                    writer.newLine();
                    writer.flush();
                    return request;
                }
            });

            String response;
            // Socket construction connects to the listener. Reader/writer adapters turn its
            // byte streams
            // into UTF-8 lines; flushing pushes the buffered request to the socket before
            // waiting for a reply.
            try (Socket socket = new Socket(LOOPBACK, server.getLocalPort());
                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
                    BufferedWriter writer = new BufferedWriter(
                            new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8))) {
                socket.setSoTimeout(Math.toIntExact(TimeUnit.SECONDS.toMillis(TIMEOUT_SECONDS)));
                writer.write(message);
                writer.newLine();
                writer.flush();
                response = reader.readLine();
            }
            serverResponse.get(TIMEOUT_SECONDS, TimeUnit.SECONDS);
            return response;
        } finally {
            // Do not leave a worker thread behind if the client or server exchange fails.
            serverExecutor.shutdownNow();
        }
    }

    /**
     * Demonstrates a UDP request and response as two separate datagrams.
     *
     * <p>
     * The server binds a {@link DatagramSocket}; unlike TCP, there is no accept
     * step. The client sends one
     * {@link DatagramPacket} addressed to the server. The server's receive call
     * yields both the payload and the
     * sender's address, which it reuses as the destination of the response packet.
     * The client receives one datagram
     * and decodes only the packet's actual byte range. Timeouts prevent an absent
     * packet from hanging the example,
     * but UDP itself does not guarantee that either packet arrives.
     *
     * @param message UTF-8 payload to send
     * @return decoded payload from the response datagram
     * @throws IOException          if a socket operation fails or times out
     * @throws InterruptedException if waiting for the server worker is interrupted
     * @throws ExecutionException   if the server worker fails
     * @throws TimeoutException     if the server worker does not finish before its
     *                              deadline
     */
    private static String udpEcho(String message)
            throws IOException, InterruptedException, ExecutionException, TimeoutException {
        ExecutorService serverExecutor = Executors.newSingleThreadExecutor();
        try (DatagramSocket server = new DatagramSocket(new InetSocketAddress(LOOPBACK, 0));
                DatagramSocket client = new DatagramSocket()) {
            server.setSoTimeout(Math.toIntExact(TimeUnit.SECONDS.toMillis(TIMEOUT_SECONDS)));
            client.setSoTimeout(Math.toIntExact(TimeUnit.SECONDS.toMillis(TIMEOUT_SECONDS)));
            Future<?> serverResponse = serverExecutor.submit(() -> {
                // A received packet records the sender address as well as its payload and exact
                // length.
                byte[] receivedBytes = new byte[1024];
                DatagramPacket request = new DatagramPacket(receivedBytes, receivedBytes.length);
                server.receive(request);
                // Echo to the source address from this packet; UDP does not maintain a
                // connected peer.
                DatagramPacket response = new DatagramPacket(
                        request.getData(), request.getLength(), request.getSocketAddress());
                server.send(response);
                return null;
            });

            byte[] requestBytes = message.getBytes(StandardCharsets.UTF_8);
            client.send(new DatagramPacket(requestBytes, requestBytes.length, LOOPBACK, server.getLocalPort()));
            byte[] responseBytes = new byte[1024];
            DatagramPacket response = new DatagramPacket(responseBytes, responseBytes.length);
            client.receive(response);
            serverResponse.get(TIMEOUT_SECONDS, TimeUnit.SECONDS);
            return new String(response.getData(), response.getOffset(), response.getLength(), StandardCharsets.UTF_8);
        } finally {
            serverExecutor.shutdownNow();
        }
    }

    /**
     * Demonstrates one complete HTTP/1.1 POST request and response over a local TCP
     * connection.
     *
     * <p>
     * The server registers {@code /echo} and starts listening before the client
     * sends a request. The client
     * explicitly selects HTTP/1.1, sets a content type, and sends the text as the
     * request body. The handler reads the
     * complete body, writes a {@code 200} status and content type, and returns
     * those bytes as the response body. The
     * HTTP client parses the response and verifies both the status and negotiated
     * protocol version.
     *
     * @param message text request body
     * @return response body returned by the handler
     * @throws IOException          if the server or HTTP exchange fails
     * @throws InterruptedException if the synchronous HTTP send is interrupted
     */
    private static String httpEcho(String message) throws IOException, InterruptedException {
        // Creating the context defines the route; start() makes the server accept HTTP
        // connections.
        HttpServer server = HttpServer.create(new InetSocketAddress(LOOPBACK, 0), 0);
        ExecutorService serverExecutor = Executors.newSingleThreadExecutor();
        server.setExecutor(serverExecutor);
        server.createContext("/echo", NetworkExamples::handleHttpEcho);
        server.start();

        try {
            // Pin the protocol to HTTP/1.1 rather than allowing HttpClient to negotiate its
            // default version.
            HttpClient client = HttpClient.newBuilder()
                    .version(HttpClient.Version.HTTP_1_1)
                    .connectTimeout(java.time.Duration.ofSeconds(TIMEOUT_SECONDS))
                    .build();
            // HttpRequest separates method, target URI, headers, and body. HttpClient
            // handles HTTP framing.
            HttpRequest request = HttpRequest.newBuilder(
                    URI.create("http://127.0.0.1:" + server.getAddress().getPort() + "/echo"))
                    .header("Content-Type", "text/plain; charset=utf-8")
                    .POST(HttpRequest.BodyPublishers.ofString(message, StandardCharsets.UTF_8))
                    .build();
            HttpResponse<String> response = client.send(request,
                    HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            if (response.statusCode() != 200 || response.version() != HttpClient.Version.HTTP_1_1) {
                throw new IOException("Unexpected HTTP response: " + response.statusCode() + " " + response.version());
            }
            return response.body();
        } finally {
            server.stop(0);
            serverExecutor.shutdownNow();
        }
    }

    private static void handleHttpEcho(HttpExchange exchange) throws IOException {
        try (exchange; var request = exchange.getRequestBody()) {
            // The server handler consumes the request body before declaring its response
            // length and writing bytes.
            byte[] body = request.readAllBytes();
            exchange.getResponseHeaders().set("Content-Type", "text/plain; charset=utf-8");
            exchange.sendResponseHeaders(200, body.length);
            try (OutputStream response = exchange.getResponseBody()) {
                response.write(body);
            }
        }
    }

    /**
     * Demonstrates the HTTP upgrade followed by one WebSocket text-message
     * exchange.
     *
     * <p>
     * The client first connects to {@code ws://...}; the WebSocket library performs
     * the HTTP upgrade handshake.
     * The client's {@code onOpen} callback runs after the handshake and sends the
     * message. The server's
     * {@code onMessage} callback receives a complete WebSocket text frame and sends
     * a frame with the same text. The
     * client's callback stores the response and releases a latch, allowing the
     * calling thread to return only after a
     * response arrives. Unlike the one-shot HTTP exchange, the upgraded connection
     * can carry additional messages in
     * either direction until it is closed.
     *
     * @param message text message sent after the upgrade handshake
     * @return text from the server's WebSocket response frame
     * @throws IOException          if the WebSocket connection or callbacks fail
     * @throws InterruptedException if a blocking connection or close is interrupted
     * @throws TimeoutException     if the handshake or response takes too long
     */
    private static String webSocketEcho(String message)
            throws IOException, InterruptedException, TimeoutException {
        int port = findAvailablePort();
        CountDownLatch responseReceived = new CountDownLatch(1);
        AtomicReference<String> responseText = new AtomicReference<>();
        AtomicReference<Exception> failure = new AtomicReference<>();

        WebSocketServer server = new WebSocketServer(new InetSocketAddress(LOOPBACK, port)) {
            @Override
            public void onOpen(WebSocket connection, ClientHandshake handshake) {
                // The handshake is complete; this example waits for the client to send its
                // first message.
            }

            @Override
            public void onClose(WebSocket connection, int code, String reason, boolean remote) {
            }

            @Override
            public void onMessage(WebSocket connection, String text) {
                // WebSocket preserves message boundaries, unlike the raw TCP example's newline
                // convention.
                connection.send(text);
            }

            @Override
            public void onError(WebSocket connection, Exception exception) {
                failure.compareAndSet(null, exception);
            }

            @Override
            public void onStart() {
            }
        };
        server.start();

        WebSocketClient client = new WebSocketClient(URI.create("ws://127.0.0.1:" + port)) {
            @Override
            public void onOpen(ServerHandshake handshake) {
                send(message);
            }

            @Override
            public void onMessage(String text) {
                responseText.set(text);
                responseReceived.countDown();
            }

            @Override
            public void onClose(int code, String reason, boolean remote) {
            }

            @Override
            public void onError(Exception exception) {
                failure.compareAndSet(null, exception);
                responseReceived.countDown();
            }
        };

        try {
            // connectBlocking returns after the WebSocket opening handshake succeeds or
            // times out.
            if (!client.connectBlocking(TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
                throw new IOException("WebSocket connection timed out");
            }
            if (!responseReceived.await(TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
                throw new TimeoutException("WebSocket echo timed out");
            }
            if (failure.get() != null) {
                throw new IOException("WebSocket exchange failed", failure.get());
            }
            // Callback threads publish the message before counting down the latch.
            return responseText.get();
        } finally {
            client.closeBlocking();
            server.stop(1000);
        }
    }

    private static int findAvailablePort() throws IOException {
        try (ServerSocket socket = new ServerSocket(0, 1, LOOPBACK)) {
            return socket.getLocalPort();
        }
    }

    /**
     * Responses received from the local server for each protocol.
     *
     * @param tcp       TCP echo response
     * @param udp       UDP echo response
     * @param http11    HTTP/1.1 response body
     * @param webSocket WebSocket message response
     */
    public record Report(String tcp, String udp, String http11, String webSocket) {
    }
}
