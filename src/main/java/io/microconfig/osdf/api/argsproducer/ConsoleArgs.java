package io.microconfig.osdf.api.argsproducer;

import io.microconfig.osdf.api.ConsoleArgsProducer;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor()
public class ConsoleArgs implements ConsoleArgsProducer {
    private final String[] args;

    public static ConsoleArgs consoleArgs(String[] args) {
        return new ConsoleArgs(args);
    }

    @Override
    public String[] args() {
        return args;
    }
}
