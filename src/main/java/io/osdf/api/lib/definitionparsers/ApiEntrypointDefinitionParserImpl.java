package io.osdf.api.lib.definitionparsers;

import io.osdf.api.lib.annotations.Public;
import io.osdf.api.lib.definitions.ApiEntrypointDefinition;
import io.osdf.api.lib.definitions.ApiGroupDefinition;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Collections.emptyList;
import static java.util.stream.Stream.of;

public class ApiEntrypointDefinitionParserImpl implements ApiEntrypointDefinitionParser {
    private final ApiGroupDefinitionParser apiGroupDefinitionParser = new ApiGroupDefinitionParserImpl();

    @Override
    public ApiEntrypointDefinition parse(Class<?> apiEntrypointClass) {
        return new ApiEntrypointDefinition(publicApiGroups(apiEntrypointClass), apiGroupDefinitions(apiEntrypointClass));
    }

    private Map<String, ApiGroupDefinition> apiGroupDefinitions(Class<?> apiEntrypointClass) {
        return of(apiEntrypointClass.getDeclaredMethods())
                    .collect(Collectors.toUnmodifiableMap(Method::getName, apiGroupDefinitionParser::parse));
    }

    private List<String> publicApiGroups(Class<?> apiEntrypointClass) {
        Public publicAnnotation = apiEntrypointClass.getAnnotation(Public.class);
        return publicAnnotation == null ? emptyList() : List.of(publicAnnotation.value());
    }
}
