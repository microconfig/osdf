package io.osdf.api.lib.definitionparsers;

import io.osdf.api.lib.definitions.MethodDefinition;
import io.osdf.api.lib.example.ExampleApiClass;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MethodDefinitionParserImplTest {
    private final MethodDefinitionParser parser = new MethodDefinitionParserImpl();

    @Test
    @SneakyThrows
    void testGenericParsing() {
        Method method = ExampleApiClass.class.getDeclaredMethod("example", String.class);
        MethodDefinition methodDefinition = parser.parse(method);
        assertEquals("Example api method", methodDefinition.getDescription());
        assertEquals(1, methodDefinition.getArgs().size());
    }
}