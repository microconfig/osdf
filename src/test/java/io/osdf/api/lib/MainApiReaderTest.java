package io.osdf.api.lib;

import io.osdf.api.MainApi;
import io.osdf.api.lib.definitionparsers.ApiEntrypointDefinitionParserImpl;
import org.junit.jupiter.api.Test;

class MainApiReaderTest {
    @Test
    void successPrintHelp() {
        new ApiEntrypointDefinitionParserImpl().parse(MainApi.class).printUsage();
    }
}