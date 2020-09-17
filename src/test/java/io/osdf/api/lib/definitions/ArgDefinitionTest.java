package io.osdf.api.lib.definitions;

import io.osdf.api.lib.annotations.Arg;
import io.osdf.api.lib.definitionparsers.ArgDefinitionParser;
import io.osdf.api.lib.definitionparsers.ArgDefinitionParserImpl;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ArgDefinitionTest {
    private final ArgDefinitionParser parser = new ArgDefinitionParserImpl();

    @Test
    @Arg(required = "name")
    @Arg(optional = "name")
    @Arg(flag = "name")
    void testUsages() {
        Arg[] args = argAnnotations();
        assertUsage(args[0], "-n name");
        assertListUsage(args[0], "-n name...");

        assertUsage(args[1], "[-n name]");
        assertListUsage(args[1], "[-n name...]");

        assertUsage(args[2], "[--name/-n]");
    }

    private void assertUsage(Arg arg, String expected) {
        String usage = parser.parse(arg, String.class).usage();
        assertEquals(expected, usage);
    }

    private void assertListUsage(Arg arg, String expected) {
        String usage = parser.parse(arg, List.class).usage();
        assertEquals(expected, usage);
    }

    @SneakyThrows
    private Arg[] argAnnotations() {
        Method method = ArgDefinitionTest.class.getDeclaredMethod("testUsages");
        return method.getAnnotationsByType(Arg.class);
    }
}