package io.osdf.api.lib.definitions;

import org.junit.jupiter.api.Test;

import java.util.List;

import static java.util.Collections.emptyList;
import static java.util.List.of;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ApiGroupDefinitionTest {
    @Test
    void removePrefix_Empty() {
        assertPrefix("", of("random"), of("random"));
    }

    @Test
    void removePrefix_Wrong() {
        assertPrefix("wrong", of("method"), emptyList());
    }

    @Test
    void removePrefix_Matches() {
        assertPrefix("method", of("method", "abc"), of("abc"));
        assertPrefix("long method", of("long", "method", "abc"), of("abc"));
    }

    private void assertPrefix(String prefix, List<String> before, List<String> after) {
        List<String> removed = new ApiGroupDefinition("name", prefix, null).removePrefix(before);
        assertEquals(after, removed);
    }
}