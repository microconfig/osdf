package io.osdf.api.lib.definitionparsers;

import io.osdf.api.lib.definitions.ApiGroupDefinition;
import io.osdf.api.lib.example.ExampleApiClass;
import io.osdf.api.lib.example.ExampleMainApiClass;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

class ApiGroupDefinitionParserImplTest {
    private final ApiGroupDefinitionParser parser = new ApiGroupDefinitionParserImpl();

    @Test
    @SneakyThrows
    void testGenericParsing() {
        Method method = ExampleMainApiClass.class.getMethod("example");

        ApiGroupDefinition apiGroup = parser.parse(method);

        assertEquals("example", apiGroup.getName());
        assertEquals("", apiGroup.getPrefix());
        assertEquals(ExampleApiClass.class, apiGroup.getApiDefinition().getApiClass());
    }

    @Test
    @SneakyThrows
    void testNamedAnnotation() {
        Method method = ExampleMainApiClass.class.getMethod("longName");

        ApiGroupDefinition apiGroup = parser.parse(method);

        assertEquals("multi word", apiGroup.getPrefix());
    }
}