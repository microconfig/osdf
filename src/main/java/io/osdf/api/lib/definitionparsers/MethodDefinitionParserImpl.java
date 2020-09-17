package io.osdf.api.lib.definitionparsers;

import io.osdf.api.lib.annotations.Arg;
import io.osdf.api.lib.annotations.Description;
import io.osdf.api.lib.definitions.ArgDefinition;
import io.osdf.api.lib.definitions.MethodDefinition;

import java.lang.reflect.Method;
import java.util.List;

import static java.util.stream.Collectors.toUnmodifiableList;
import static java.util.stream.IntStream.range;

public class MethodDefinitionParserImpl implements MethodDefinitionParser {
    private final ArgDefinitionParser apiDefinitionParser = new ArgDefinitionParserImpl();

    @Override
    public MethodDefinition parse(Method method) {
        return new MethodDefinition(method, description(method), argDefinitions(method));
    }

    private List<ArgDefinition> argDefinitions(Method method) {
        Arg[] argAnnotations = method.getAnnotationsByType(Arg.class);
        Class<?>[] argTypes = method.getParameterTypes();
        return range(0, argAnnotations.length)
                .mapToObj(ind -> apiDefinitionParser.parse(argAnnotations[ind], argTypes[ind]))
                .collect(toUnmodifiableList());
    }

    private String description(Method method) {
        Description descriptionAnnotation = method.getAnnotation(Description.class);
        return descriptionAnnotation == null ? "" : descriptionAnnotation.value();
    }
}
