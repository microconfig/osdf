package io.microconfig.osdf.api.v2;

import io.microconfig.osdf.exceptions.OSDFException;
import lombok.RequiredArgsConstructor;

import java.lang.reflect.Method;
import java.util.List;

import static java.lang.Character.toUpperCase;
import static java.lang.String.join;
import static java.util.Arrays.copyOfRange;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toUnmodifiableList;
import static java.util.stream.Stream.of;

@RequiredArgsConstructor
public class ApiReader {
    private final Class<?> apiClass;

    public static ApiReader reader(Class<?> apiClass) {
        return new ApiReader(apiClass);
    }

    public Method methodByName(String name) {
        String camelCaseName = toCamelCase(name);
        return of(apiClass.getMethods())
                .filter(method -> method.getName().equals(camelCaseName))
                .findFirst()
                .orElseThrow(() -> new OSDFException("Unknown method " + name));
    }

    private String toCamelCase(String name) {
        if (!name.contains("-")) return name;
        String[] tokens = name.split("-");
        List<String> upperCaseTokens = stream(copyOfRange(tokens, 1, tokens.length))
                .map(s -> toUpperCase(s.charAt(0)) + s.substring(1).toLowerCase())
                .collect(toUnmodifiableList());
        return tokens[0] + join("", upperCaseTokens);
    }
}
