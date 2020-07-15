package io.osdf.api.lib;

import io.osdf.api.lib.example.ExampleMainApiClass;
import org.junit.jupiter.api.Test;

import static io.osdf.api.lib.MainApiReader.apiInfo;

class MainApiReaderTest {
    @Test
    void successPrintHelp() {
        apiInfo(ExampleMainApiClass.class).printHelp();
    }
}