package foundation.template;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GreetingTest {
    @Test
    void createsGreetingAndExposesLombokGetter() {
        Greeting greeting = new Greeting("Java");

        assertEquals("Java", greeting.getRecipient());
        assertEquals("Hello, Java!", greeting.message());
    }
}