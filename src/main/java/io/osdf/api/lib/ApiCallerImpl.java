package io.osdf.api.lib;

import io.osdf.common.exceptions.OSDFException;
import lombok.Builder;
import lombok.Singular;

import java.util.List;
import java.util.Map;

@Builder
public class ApiCallerImpl implements ApiCaller {
    private final ApiCallFinder finder;
    @Singular("addImpl")
    private final Map<Class<?>, Object> implementations;

    @Override
    public void call(List<String> args) {
        ApiCall apiCall = finder.find(args);
        Class<?> apiClass = apiCall.getApiClass();
        Object implementation = implementations.get(apiClass);
        if (implementation == null) throw new OSDFException("No implementation for api class " + apiClass.getSimpleName());
        apiCall.invoke(implementation);
    }
}
