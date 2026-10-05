package foundation.io.java21;

import foundation.io.App;

/** Runs the shared examples and the Java 21 virtual-thread extension. */
public final class Java21App {
    private Java21App() {
    }

    /**
     * Runs the baseline demos followed by the virtual-thread TCP demo.
     *
     * <p>
     * The first call is identical to the Java 17 entry point. The additional call
     * runs a separate one-connection
     * TCP server and prints whether its request handler actually ran on a virtual
     * thread.
     *
     * @param args unused command-line arguments
     * @throws Exception if a shared example or the TCP exchange fails
     */
    public static void main(String[] args) throws Exception {
        App.main(args);
        VirtualThreadTcpExample.Result result = VirtualThreadTcpExample.run("virtual thread request");
        System.out.println("Virtual-thread TCP echo: " + result.response());
        System.out.println("Handled by virtual thread: " + result.handledByVirtualThread());
    }
}
