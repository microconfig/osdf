package io.osdf.api.lib.definitions;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;

@Getter
@RequiredArgsConstructor
public class ApiDefinition {
    private final Class<?> apiClass;
    private final List<String> publicMethods;
    private final Map<String, MethodDefinition> methods;
}
