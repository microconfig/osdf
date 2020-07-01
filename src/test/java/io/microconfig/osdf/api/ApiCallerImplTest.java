package io.microconfig.osdf.api;

import io.microconfig.osdf.api.example.ExampleApiClass;
import io.microconfig.osdf.api.example.ExampleApiClassImpl;
import io.microconfig.osdf.api.example.ExampleMainApiClass;
import io.osdf.common.exceptions.OSDFException;
import io.osdf.api.lib.ApiCallerImpl;
import org.junit.jupiter.api.Test;

import static io.osdf.api.lib.ApiCallFinder.finder;
import static java.util.List.of;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ApiCallerImplTest {
    @Test
    void testCall() {
        ApiCallerImpl caller = ApiCallerImpl.builder()
                .finder(finder(ExampleMainApiClass.class))
                .addImpl(ExampleApiClass.class, new ExampleApiClassImpl())
                .build();

        caller.call(of("example", "arg"));
        assertThrows(OSDFException.class, () -> caller.call(of("unknown", "method")));
    }

    @Test
    void noImplementationsCall() {
        ApiCallerImpl caller = ApiCallerImpl.builder()
                .finder(finder(ExampleMainApiClass.class))
                .build();
        assertThrows(RuntimeException.class, () -> caller.call(of("example", "arg")));
    }
}