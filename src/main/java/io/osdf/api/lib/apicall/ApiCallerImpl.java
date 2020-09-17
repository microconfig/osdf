package io.osdf.api.lib.apicall;

import io.osdf.api.lib.definitionparsers.ApiEntrypointDefinitionParser;
import io.osdf.api.lib.definitionparsers.ApiEntrypointDefinitionParserImpl;
import io.osdf.common.exceptions.OSDFException;
import lombok.Builder;
import lombok.Singular;

import java.util.List;
import java.util.Map;

import static io.osdf.api.lib.apicall.ApiCallInvoker.apiCallInvoker;
import static io.osdf.api.lib.apicall.ApiCallResolver.apiCallResolver;

@Builder
public class ApiCallerImpl implements ApiCaller {
    private final ApiEntrypointDefinitionParser parser = new ApiEntrypointDefinitionParserImpl();
    @Singular("addImpl")
    private final Map<Class<?>, Object> implementations;

    @Override
    public void call(Class<?> apiEntrypointClass, List<String> args) {
        var apiEntrypointDefinition = parser.parse(apiEntrypointClass);
        ApiCall apiCall = apiCallResolver().resolve(apiEntrypointDefinition, args);

        Class<?> apiClass = apiCall.getApiDefinition().getApiClass();
        Object implementation = implementations.get(apiClass);
        if (implementation == null) throw new OSDFException("No implementation for api class " + apiClass.getSimpleName());

        apiCallInvoker().invoke(apiCall, implementation);
    }
}
