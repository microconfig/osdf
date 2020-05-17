package io.microconfig.osdf.api.example;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ExampleApiClassImpl implements ExampleApiClass {
    @Override
    public void example(String arg) {
        assertEquals("arg", arg);
    }

    @Override
    public void camelCase(String arg) {
    }
}