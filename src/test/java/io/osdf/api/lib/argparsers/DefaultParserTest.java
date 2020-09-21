package io.osdf.api.lib.argparsers;

import io.osdf.api.lib.ApiException;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;

import static java.util.List.of;
import static org.junit.jupiter.api.Assertions.*;

class DefaultParserTest {
    @Test
    void testDefaultParsers() {
        assertParser(String.class, "test", "test");
        assertParser(Integer.class, 1, "1");
        assertParser(Path.class, Path.of("/a/b/c"), "/a/b/c");
        assertParser(Boolean.class, true, "true");
    }

    @Test
    void testUnknownParser() {
        assertThrows(ApiException.class, () -> new DefaultParser(Object.class));
    }

    @Test
    void testListParser() {
        DefaultParser parser = new DefaultParser(Integer.class);
        assertEquals(of(1, 2, 3), parser.parseList("1,2,3"));
        assertEquals(of(1, 2, 3), parser.parseList(of("1", "2", "3")));
    }

    private <T> void assertParser(Class<? extends T> parserClass, T expected, String arg) {
        assertEquals(expected, new DefaultParser(parserClass).parse(arg));
        assertNull(new DefaultParser(parserClass).parse(null));
    }
}