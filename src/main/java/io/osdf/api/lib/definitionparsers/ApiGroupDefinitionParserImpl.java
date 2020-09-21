package io.osdf.api.lib.definitionparsers;

import io.osdf.api.lib.ApiException;
import io.osdf.api.lib.annotations.ApiGroup;
import io.osdf.api.lib.annotations.Named;
import io.osdf.api.lib.definitions.ApiDefinition;
import io.osdf.api.lib.definitions.ApiGroupDefinition;

import java.lang.reflect.Method;

public class ApiGroupDefinitionParserImpl implements ApiGroupDefinitionParser {
    private final ApiDefinitionParser apiDefinitionParser = new ApiDefinitionParserImpl();

    @Override
    public ApiGroupDefinition parse(Method method) {
        return new ApiGroupDefinition(method.getName(), prefix(method), apiDefinition(method));
    }

    private ApiDefinition apiDefinition(Method method) {
        ApiGroup apiGroup = method.getAnnotation(ApiGroup.class);
        if (apiGroup == null) throw new ApiException("ApiGroup annotation is required for method " + method.getName());
        return apiDefinitionParser.parse(apiGroup.value());
    }

    private String prefix(Method method) {
        Named named = method.getAnnotation(Named.class);
        if (named == null) return "";
        if (named.as().isEmpty()) return method.getName();
        return named.as();
    }
}
