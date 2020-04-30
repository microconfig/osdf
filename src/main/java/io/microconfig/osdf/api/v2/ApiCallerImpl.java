package io.microconfig.osdf.api.v2;

import lombok.Builder;
import lombok.Singular;

import java.util.List;
import java.util.Map;

@Builder
public class ApiCallerImpl implements NewApiCaller {
    private final ApiCallFinder finder;
    @Singular("addImpl")
    private final Map<Class<?>, Object> implementations;

    @Override
    public void call(List<String> args) {
        ApiCall apiCall = finder.find(args);
        Class<?> apiClass = apiCall.getApiClass();
        Object implementation = implementations.get(apiClass);
        if (implementation == null) throw new RuntimeException("No implementation for api class " + apiClass.getSimpleName());
        apiCall.invoke(implementation);
    }
}
