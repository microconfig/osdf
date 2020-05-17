package io.microconfig.osdf.api;

import io.microconfig.osdf.api.example.ExampleApiClass;
import io.microconfig.osdf.api.example.ExampleMainApiClass;
import io.microconfig.osdf.exceptions.OSDFException;
import org.junit.jupiter.api.Test;

import static io.microconfig.osdf.api.ApiCallFinder.finder;
import static java.util.List.of;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ApiCallFinderTest {
    @Test
    void successFind() throws NoSuchMethodException {
        ApiCallFinder finder = finder(ExampleMainApiClass.class);
        ApiCall apiCall = finder.find(of("example", "arg"));
        assertEquals(ExampleApiClass.class, apiCall.getApiClass());
        assertEquals(of("arg"), apiCall.getArgs());
        assertEquals(ExampleApiClass.class.getMethod("example", String.class), apiCall.getMethod());
    }

    @Test
    void exceptionIfMethodUnknown() {
        ApiCallFinder finder = finder(ExampleMainApiClass.class);
        assertThrows(OSDFException.class, () -> finder.find(of("unknown", "method")));
    }
}