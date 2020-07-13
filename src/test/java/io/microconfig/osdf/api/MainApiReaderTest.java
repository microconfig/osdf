package io.microconfig.osdf.api;

import io.microconfig.osdf.api.example.ExampleMainApiClass;
import org.junit.jupiter.api.Test;

import static io.osdf.api.lib.MainApiReader.apiInfo;

class MainApiReaderTest {
    @Test
    void successPrintHelp() {
        apiInfo(ExampleMainApiClass.class).printHelp();
    }
}