package io.osdf.api.lib;

import io.osdf.api.lib.annotations.ApiCommand;
import io.osdf.api.lib.annotations.Hidden;
import io.osdf.common.exceptions.OSDFException;
import lombok.RequiredArgsConstructor;

import java.lang.reflect.Method;
import java.util.List;

import static java.lang.Character.toUpperCase;
import static java.lang.String.join;
import static java.util.Arrays.copyOfRange;
import static java.util.Arrays.stream;
import static java.util.Comparator.comparingInt;
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

    public List<Method> methods() {
        return of(apiClass.getMethods())
                .filter(method -> method.getAnnotation(Hidden.class) == null)
                .sorted(comparingInt(ApiReader::orderOfMethod))
                .collect(toUnmodifiableList());
    }

    private static int orderOfMethod(Method method) {
        ApiCommand apiCommand = method.getAnnotation(ApiCommand.class);
        return apiCommand == null ? 100 : apiCommand.order();
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
