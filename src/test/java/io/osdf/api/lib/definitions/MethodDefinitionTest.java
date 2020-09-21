package io.osdf.api.lib.definitions;

import io.osdf.api.lib.definitionparsers.MethodDefinitionParserImpl;
import io.osdf.api.lib.example.ExampleApiClass;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MethodDefinitionTest {
    @Test
    @SneakyThrows
    void tesUsageHelp() {
        MethodDefinition methodDefinition = new MethodDefinitionParserImpl().
                parse(ExampleApiClass.class.getDeclaredMethod("example", String.class));
        assertEquals("[-f first]", methodDefinition.usageHelp());
    }
}