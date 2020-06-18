package io.microconfig.osdf.api;

import io.microconfig.osdf.api.example.ExampleMainApiClass;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static io.microconfig.osdf.api.ImportPrefix.importPrefix;
import static java.util.List.of;
import static org.junit.jupiter.api.Assertions.assertEquals;

class GroupPrefixTest {
    @Test
    void noPrefix() throws NoSuchMethodException {
        ImportPrefix prefix = importPrefix(method("example"));
        assertEquals("", prefix.toString());
        assertEquals(of(), prefix.toList());
    }

    @Test
    void withPrefix() throws NoSuchMethodException {
        ImportPrefix prefix = importPrefix(method("longName"));
        assertEquals("multi word", prefix.toString());
        assertEquals(of("multi", "word"), prefix.toList());
    }

    @Test
    void removePrefix() throws NoSuchMethodException {
        ImportPrefix prefix = importPrefix(method("longName"));
        assertEquals(of("args"), prefix.removePrefix(of("multi", "word", "args")));
        assertEquals(of(), prefix.removePrefix(of("wrong", "call")));
    }

    @Test
    void doNothingIfPrefixIsEmpty() throws NoSuchMethodException {
        ImportPrefix prefix = importPrefix(method("example"));
        assertEquals(of("multi", "word", "args"), prefix.removePrefix(of("multi", "word", "args")));
    }

    private Method method(String name) throws NoSuchMethodException {
        return ExampleMainApiClass.class.getMethod(name);
    }
}