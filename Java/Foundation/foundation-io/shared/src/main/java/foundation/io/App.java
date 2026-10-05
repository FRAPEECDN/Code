package foundation.io;

import foundation.io.files.FileIoExamples;
import foundation.io.network.NetworkExamples;
import foundation.io.serialization.SerializationExamples;

/**
 * Runs the shared file-I/O, serialization, and network protocol examples.
 *
 * <p>
 * The application has no external configuration or server dependency. It runs
 * the file and serialization
 * round-trips first, then starts four local echo servers in turn. Each network
 * client prints the response it receives
 * from its corresponding server.
 */
public final class App {
    private App() {
    }

    /**
     * Executes each example locally and prints the values returned by its
     * demonstration.
     *
     * <p>
     * The file example cleans up its temporary directory before returning. The
     * serialization example prints
     * both encoded forms and reconstructed values. The network example closes every
     * listener and client after one
     * request/response exchange.
     *
     * @param args unused command-line arguments
     * @throws Exception if an example cannot complete its I/O or network exchange
     */
    public static void main(String[] args) throws Exception {
        // File operations happen synchronously; the example removes its temporary files
        // before returning.
        FileIoExamples.Report files = FileIoExamples.run();
        System.out.println("File I/O: " + files);

        // The same message is encoded in two formats and reconstructed twice.
        SerializationExamples.Report serialization = SerializationExamples.run();
        System.out.println("Java serialization bytes: " + serialization.objectStreamBytes());
        System.out.println("Object stream round trip: " + serialization.objectRoundTrip());
        System.out.println("JSON: " + serialization.json());
        System.out.println("JSON round trip: " + serialization.jsonRoundTrip());

        // Each protocol starts a loopback server, sends one request, prints its
        // response, and shuts down.
        NetworkExamples.Report network = NetworkExamples.run();
        System.out.println("TCP echo: " + network.tcp());
        System.out.println("UDP echo: " + network.udp());
        System.out.println("HTTP/1.1 echo: " + network.http11());
        System.out.println("WebSocket echo: " + network.webSocket());
    }
}
