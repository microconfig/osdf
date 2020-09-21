package io.osdf.api.lib.apicall;

import io.osdf.api.lib.ApiException;
import io.osdf.api.lib.example.ExampleApiClass;
import io.osdf.api.lib.example.ExampleApiClassImpl;
import io.osdf.api.lib.example.ExampleMainApiClass;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static java.util.List.of;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class ApiCallerImplTest {
    private ExampleApiClassImpl impl;
    private ApiCaller apiCaller;

    @Test
    void okCall() {
        apiCaller.call(ExampleMainApiClass.class, of("example", "arg"));

        verify(impl, times(1)).example("arg");
    }

    @Test
    void unknownMethod() {
        assertThrows(ApiException.class, () -> apiCaller.call(ExampleMainApiClass.class, of("unknown", "method")));
    }

    @Test
    void noImplementations() {
        assertThrows(ApiException.class, () -> ApiCallerImpl.builder().build().call(ExampleMainApiClass.class, of("unknown", "method")));
    }

    @BeforeEach
    void setApiCaller() {
        impl = spy(new ExampleApiClassImpl());
        apiCaller = ApiCallerImpl.builder()
                .addImpl(ExampleApiClass.class, impl)
                .build();
    }
}