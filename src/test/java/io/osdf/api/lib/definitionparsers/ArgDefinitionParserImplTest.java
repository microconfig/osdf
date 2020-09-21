package io.osdf.api.lib.definitionparsers;

import io.osdf.api.lib.annotations.Arg;
import io.osdf.api.lib.argparsers.ArgParser;
import io.osdf.api.lib.argparsers.DefaultParser;
import io.osdf.api.lib.definitions.ArgDefinition;
import io.osdf.api.lib.definitions.ArgType;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static io.osdf.api.lib.definitions.ArgType.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ArgDefinitionParserImplTest {
    private final ArgDefinitionParser parser = new ArgDefinitionParserImpl();

    @Test
    @Arg(required = "name", d = "description")
    @Arg(optional = "name", d = "description")
    @Arg(flag = "name", d = "description")
    void testGenericParsing() {
        Arg[] annotations = argAnnotations("testGenericParsing");

        assertArgDefinition(parser.parse(annotations[0], String.class), REQUIRED);
        assertArgDefinition(parser.parse(annotations[1], String.class), OPTIONAL);
        assertArgDefinition(parser.parse(annotations[2], Boolean.class), FLAG);
    }

    @Test
    @Arg(required = "shortName/name", d = "description")
    void testShortNameOverride() {
        Arg[] annotations = argAnnotations("testShortNameOverride");

        ArgDefinition arg = parser.parse(annotations[0], String.class);

        assertEquals("shortName", arg.getShortName());
        assertEquals("name", arg.getName());
    }

    @Test
    @Arg(required = "name", d = "description", p = CustomParser.class)
    void testCustomParser() {
        Arg[] annotations = argAnnotations("testCustomParser");

        ArgDefinition arg = parser.parse(annotations[0], String.class);

        assertEquals("parsed", arg.getParser().parse("arg"));
    }

    @SneakyThrows
    private Arg[] argAnnotations(String name) {
        Method method = ArgDefinitionParserImplTest.class.getDeclaredMethod(name);
        return method.getAnnotationsByType(Arg.class);
    }

    private void assertArgDefinition(ArgDefinition arg, ArgType required) {
        assertEquals("name", arg.getName());
        assertEquals("n", arg.getShortName());
        assertEquals(required, arg.getArgType());
        assertTrue(arg.getParser() instanceof DefaultParser);
        assertEquals("description", arg.getDescription());
    }

    public static class CustomParser implements ArgParser<String> {
        @Override
        public String parse(String arg) {
            return "parsed";
        }
    }
}