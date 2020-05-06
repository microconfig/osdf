package io.microconfig.osdf.api;

import io.microconfig.osdf.api.example.ExampleMainApiClass;
import org.junit.jupiter.api.Test;

import static io.microconfig.osdf.api.MainApiReader.apiInfo;

class MainApiReaderTest {
    @Test
    void successPrintHelp() {
        apiInfo(ExampleMainApiClass.class).printHelp();
    }
}