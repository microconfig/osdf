package io.osdf.api.lib.apicall;

import io.osdf.api.lib.definitionparsers.ApiEntrypointDefinitionParserImpl;
import io.osdf.api.lib.example.ExampleApiClass;
import io.osdf.api.lib.example.ExampleApiClassImpl;
import io.osdf.api.lib.example.ExampleMainApiClass;
import io.osdf.common.exceptions.OSDFException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.osdf.api.lib.apicall.ApiCallInvoker.apiCallInvoker;
import static io.osdf.api.lib.apicall.ApiCallResolver.apiCallResolver;
import static java.util.List.of;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ApiCallInvokerTest {
    private final ApiCallInvoker apiCallInvoker = apiCallInvoker();

    @Test
    void testSuccessfulCall() {
        ApiCall apiCall = resolve(of("example", "arg"));

        apiCallInvoker.invoke(apiCall, new ExampleApiClassImpl());
    }

    @Test
    void testWrongArgsCall() {
        ApiCall apiCall = resolve(of("example", "arg", "redundantArgs"));

        apiCallInvoker.invoke(apiCall, new ExampleApiClassImpl());
    }

    @Test
    void throwNonOsdfException() {
        ApiCall apiCall = resolve(of("example", "arg"));

        try {
            apiCallInvoker.invoke(apiCall, new ApiExceptionImpl(new RuntimeException()));
        } catch (OSDFException e) {
            throw new RuntimeException("OSDF exception is not expected");
        } catch (RuntimeException ignored) {
            return;
        }
        throw new RuntimeException("Runtime exception should be thrown");
    }

    @Test
    void passthroughForOsdfException() {
        ApiCall apiCall = resolve(of("example", "arg"));

        assertThrows(OSDFException.class, () -> apiCallInvoker.invoke(apiCall, new ApiExceptionImpl(new OSDFException())));
    }

    private ApiCall resolve(List<String> args) {
        return apiCallResolver().resolve(new ApiEntrypointDefinitionParserImpl().parse(ExampleMainApiClass.class), args);
    }


    private static class ApiExceptionImpl implements ExampleApiClass {
        private final RuntimeException exception;

        public ApiExceptionImpl(RuntimeException e) {
            this.exception = e;
        }

        @Override
        public void example(String arg) {
            throw exception;
        }

        @Override
        public void camelCase(String arg) {
            throw exception;
        }
    }
}