package foundation.io.java21;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Demonstrates handling a TCP connection on a Java 21 virtual thread.
 *
 * <p>
 * The listener and client use ordinary blocking socket APIs. The listener's
 * accept-and-echo task is submitted to
 * {@link Executors#newVirtualThreadPerTaskExecutor()}, so blocking while
 * waiting for a client does not require a
 * dedicated operating-system thread. Virtual threads change how blocking tasks
 * are scheduled; they do not change
 * TCP's byte-stream semantics or add message boundaries.
 */
public final class VirtualThreadTcpExample {
    private VirtualThreadTcpExample() {
    }

    /**
     * Starts a one-connection TCP echo server whose handler runs in a virtual
     * thread.
     *
     * <ol>
     * <li>The server binds to loopback and an ephemeral port, then submits its
     * accept-and-handle task to the virtual
     * thread-per-task executor.</li>
     * <li>The caller acts as the client: it connects to the server port, writes one
     * UTF-8 line, and flushes it.</li>
     * <li>The handler accepts the TCP connection, reads through the newline
     * delimiter, echoes the line, and records
     * {@link Thread#isVirtual()} to make the execution model observable.</li>
     * <li>The client reads the echoed line, waits for the handler result, and
     * closes both sockets and the executor.</li>
     * </ol>
     *
     * @param message text sent by the client
     * @return the echoed text and whether the handler used a virtual thread
     * @throws IOException          if socket setup or communication fails
     * @throws InterruptedException if the waiting thread is interrupted
     * @throws ExecutionException   if the virtual-thread handler fails
     * @throws TimeoutException     if the handler does not finish within five
     *                              seconds
     */
    public static Result run(String message)
            throws IOException, InterruptedException, ExecutionException, TimeoutException {
        try (ServerSocket server = new ServerSocket(0, 1, InetAddress.getLoopbackAddress());
                var handlers = Executors.newVirtualThreadPerTaskExecutor()) {
            // submit starts a virtual thread that can block in accept() while the caller
            // becomes the client.
            var response = handlers.submit(() -> {
                try (Socket socket = server.accept();
                        BufferedReader reader = new BufferedReader(
                                new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
                        BufferedWriter writer = new BufferedWriter(
                                new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8))) {
                    socket.setSoTimeout(5000);
                    String request = reader.readLine();
                    writer.write(request);
                    writer.newLine();
                    writer.flush();
                    return new Result(request, Thread.currentThread().isVirtual());
                }
            });

            // Connecting establishes a TCP stream. The newline is this protocol example's
            // application framing.
            try (Socket client = new Socket(InetAddress.getLoopbackAddress(), server.getLocalPort());
                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(client.getInputStream(), StandardCharsets.UTF_8));
                    BufferedWriter writer = new BufferedWriter(
                            new OutputStreamWriter(client.getOutputStream(), StandardCharsets.UTF_8))) {
                client.setSoTimeout(5000);
                writer.write(message);
                writer.newLine();
                writer.flush();
                String echoed = reader.readLine();
                Result serverResult = response.get(5, TimeUnit.SECONDS);
                return new Result(echoed, serverResult.handledByVirtualThread());
            }
        }
    }

    /**
     * Result of the virtual-thread TCP echo exchange.
     *
     * @param response               text echoed to the client
     * @param handledByVirtualThread whether the server handler ran on a virtual
     *                               thread
     */
    public record Result(String response, boolean handledByVirtualThread) {
    }
}
