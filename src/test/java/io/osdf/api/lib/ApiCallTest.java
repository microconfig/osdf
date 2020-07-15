package io.osdf.api.lib;

import io.osdf.api.lib.example.ExampleApiClass;
import io.osdf.api.lib.example.ExampleApiClassImpl;
import io.osdf.common.exceptions.OSDFException;
import org.junit.jupiter.api.Test;

import static io.osdf.api.lib.ApiCall.apiCall;
import static java.util.List.of;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ApiCallTest {
    @Test
    void success() {
        apiCall(ExampleApiClass.class, "example", of("arg"))
                .invoke(new ExampleApiClassImpl());
    }

    @Test
    void exceptionOnWrongMethod() {
        assertThrows(OSDFException.class, () -> apiCall(ExampleApiClass.class, "wrongMethod", of("arg")));
    }

    @Test
    void exceptionOnWrongArgs() {
        assertThrows(OSDFException.class, () ->
                apiCall(ExampleApiClass.class, "wrongMethod", of("arg1", "arg2"))
                .invoke(new ExampleApiClassImpl()));
    }
}