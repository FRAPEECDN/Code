package foundation.io.network;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class NetworkExamplesTest {
    @Test
    void exchangesMessagesWithTcpUdpHttp11AndWebSocketServers() throws Exception {
        NetworkExamples.Report report = NetworkExamples.run();

        assertEquals("tcp request", report.tcp());
        assertEquals("udp request", report.udp());
        assertEquals("http/1.1 request", report.http11());
        assertEquals("websocket request", report.webSocket());
    }
}
