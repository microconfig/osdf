package io.osdf.api.lib.apicall;

import io.osdf.api.lib.ApiException;
import io.osdf.api.lib.definitionparsers.ApiEntrypointDefinitionParserImpl;
import io.osdf.api.lib.definitions.ApiEntrypointDefinition;
import io.osdf.api.lib.example.ExampleApiClass;
import io.osdf.api.lib.example.ExampleMainApiClass;
import org.junit.jupiter.api.Test;

import static io.osdf.api.lib.apicall.ApiCallResolver.apiCallResolver;
import static java.util.List.of;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ApiCallResolverTest {
    private final ApiEntrypointDefinition api = new ApiEntrypointDefinitionParserImpl().parse(ExampleMainApiClass.class);

    @Test
    void resolveSuccessfully() {
        ApiCall apiCall = apiCallResolver().resolve(api, of("example", "arg"));

        assertApiCall(apiCall, "example");
    }

    @Test
    void resolveWithPrefix() {
        ApiCall apiCall = apiCallResolver().resolve(api, of("multi", "word", "example", "arg"));

        assertApiCall(apiCall, "example");
    }

    @Test
    void resolveDashedMethodCall() {
        ApiCall apiCall = apiCallResolver().resolve(api, of("camel-case", "arg"));

        assertApiCall(apiCall, "camelCase");
    }

    @Test
    void failOnWrongMethod() {
        assertThrows(ApiException.class, () -> apiCallResolver().resolve(api, of("unknownMethod", "arg")));
    }

    private void assertApiCall(ApiCall apiCall, String camelCase) {
        assertEquals(ExampleApiClass.class, apiCall.getApiDefinition().getApiClass());
        assertEquals(camelCase, apiCall.getMethodDefinition().getMethod().getName());
        assertEquals(of("arg"), apiCall.getArgs());
    }
}