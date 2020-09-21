package io.osdf.api.lib.definitions;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.lang.reflect.Method;
import java.util.List;

import static java.util.stream.Collectors.joining;

@Getter
@RequiredArgsConstructor
public class MethodDefinition {
    private final Method method;
    private final String description;
    private final List<ArgDefinition> args;

    public String usageHelp() {
        return args.stream()
                .map(ArgDefinition::usage)
                .collect(joining(" "));
    }
}
