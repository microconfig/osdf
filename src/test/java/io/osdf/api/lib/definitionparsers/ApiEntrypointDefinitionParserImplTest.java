package io.osdf.api.lib.definitionparsers;

import io.osdf.api.lib.definitions.ApiEntrypointDefinition;
import io.osdf.api.lib.example.ExampleMainApiClass;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ApiEntrypointDefinitionParserImplTest {
    private final ApiEntrypointDefinitionParser parser = new ApiEntrypointDefinitionParserImpl();

    @Test
    void testGenericParsing() {
        ApiEntrypointDefinition apiEntrypoint = parser.parse(ExampleMainApiClass.class);

        assertEquals(List.of("example", "longName"), apiEntrypoint.getPublicApiGroups());
        assertEquals(2, apiEntrypoint.getPublicApiGroups().size());
    }
}