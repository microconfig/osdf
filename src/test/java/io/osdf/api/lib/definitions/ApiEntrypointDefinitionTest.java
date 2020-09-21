package io.osdf.api.lib.definitions;

import io.osdf.api.lib.definitionparsers.ApiEntrypointDefinitionParserImpl;
import io.osdf.api.lib.example.ExampleMainApiClass;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ApiEntrypointDefinitionTest {

    @Test
    void testCommands() {
        List<String> commands = new ApiEntrypointDefinitionParserImpl().parse(ExampleMainApiClass.class).commands();

        assertEquals(Set.of("example", "camelCase"), new HashSet<>(commands));
    }

    @Test
    void noErrorsOn_printUsage() {
        new ApiEntrypointDefinitionParserImpl().parse(ExampleMainApiClass.class).printUsage();
    }
}