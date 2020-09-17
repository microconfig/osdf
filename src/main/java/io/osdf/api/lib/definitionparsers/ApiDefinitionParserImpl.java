package io.osdf.api.lib.definitionparsers;

import io.osdf.api.lib.annotations.Public;
import io.osdf.api.lib.definitions.ApiDefinition;
import io.osdf.api.lib.definitions.MethodDefinition;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static java.util.Collections.emptyList;
import static java.util.List.of;
import static java.util.stream.Collectors.toUnmodifiableMap;

public class ApiDefinitionParserImpl implements ApiDefinitionParser {
    private final MethodDefinitionParser methodDefinitionParser = new MethodDefinitionParserImpl();

    @Override
    public ApiDefinition parse(Class<?> apiClass) {
        return new ApiDefinition(apiClass, publicMethods(apiClass), methods(apiClass));
    }

    private List<String> publicMethods(Class<?> apiClass) {
        Public publicAnnotation = apiClass.getAnnotation(Public.class);
        return publicAnnotation == null ? emptyList() : of(publicAnnotation.value());
    }

    private Map<String, MethodDefinition> methods(Class<?> apiClass) {
        return Stream.of(apiClass.getDeclaredMethods())
                    .collect(toUnmodifiableMap(Method::getName, methodDefinitionParser::parse));
    }
}
