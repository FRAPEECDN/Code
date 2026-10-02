package foundation.template;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class Greeting {
    private final String recipient;

    public String message() {
        return "Hello, " + recipient + "!";
    }
}