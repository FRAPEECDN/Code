package foundation.io.java21;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class VirtualThreadTcpExampleTest {
    @Test
    void servesTcpRequestOnAVirtualThread() throws Exception {
        VirtualThreadTcpExample.Result result = VirtualThreadTcpExample.run("hello virtual thread");

        assertEquals("hello virtual thread", result.response());
        assertTrue(result.handledByVirtualThread());
    }
}
