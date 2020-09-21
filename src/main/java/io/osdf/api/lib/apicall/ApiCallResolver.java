package io.osdf.api.lib.apicall;

import io.osdf.api.lib.ApiException;
import io.osdf.api.lib.definitions.ApiDefinition;
import io.osdf.api.lib.definitions.ApiEntrypointDefinition;
import io.osdf.api.lib.definitions.ApiGroupDefinition;
import io.osdf.api.lib.definitions.MethodDefinition;

import java.util.List;
import java.util.Objects;

import static java.lang.Character.toUpperCase;
import static java.lang.String.join;
import static java.util.Arrays.copyOfRange;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toUnmodifiableList;

public class ApiCallResolver {
    public static ApiCallResolver apiCallResolver() {
        return new ApiCallResolver();
    }

    public ApiCall resolve(ApiEntrypointDefinition apiEntrypointDefinition, List<String> argsWithPrefix) {
        return apiEntrypointDefinition
                .getApiGroupDefinitions().values().stream()
                .map(apiGroupDefinition -> resolveApiGroup(apiGroupDefinition, argsWithPrefix))
                .filter(Objects::nonNull)
                .findFirst()
                .orElseThrow(() -> new ApiException("Method not found"));
    }

    private ApiCall resolveApiGroup(ApiGroupDefinition apiGroupDefinition, List<String> argsWithPrefix) {
        List<String> args = apiGroupDefinition.removePrefix(argsWithPrefix);
        if (args.isEmpty()) return null;

        String methodName = toCamelCase(args.get(0));
        List<String> methodArgs = args.subList(1, args.size());

        ApiDefinition apiDefinition = apiGroupDefinition.getApiDefinition();
        MethodDefinition methodDefinition = apiDefinition.getMethods().get(methodName);
        if (methodDefinition == null) return null;

        return new ApiCall(apiDefinition, methodDefinition, methodArgs);
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
