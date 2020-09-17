package io.osdf.api.lib.definitionparsers;

import io.osdf.api.lib.definitions.ApiDefinition;
import io.osdf.api.lib.example.ExampleApiClass;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ApiDefinitionParserImplTest {
    private final ApiDefinitionParser parser = new ApiDefinitionParserImpl();

    @Test
    void testGenericParsing() {
        ApiDefinition apiDefinition = parser.parse(ExampleApiClass.class);
        assertEquals(ExampleApiClass.class, apiDefinition.getApiClass());
        assertEquals(List.of("example", "camelCase"), apiDefinition.getPublicMethods());
        assertEquals(2, apiDefinition.getMethods().size());
    }
}