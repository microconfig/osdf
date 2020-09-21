package io.osdf.api.lib.apicall;

import io.osdf.api.lib.definitions.ApiDefinition;
import io.osdf.api.lib.definitions.MethodDefinition;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class ApiCall {
    private final ApiDefinition apiDefinition;
    private final MethodDefinition methodDefinition;
    private final List<String> args;

    public static ApiCall apiCall(ApiDefinition apiDefinition, MethodDefinition methodDefinition, List<String> args) {
        return new ApiCall(apiDefinition, methodDefinition, args);
    }
}
