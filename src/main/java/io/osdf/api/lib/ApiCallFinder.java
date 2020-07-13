package io.osdf.api.lib;

import io.osdf.api.lib.annotations.Import;
import io.osdf.common.exceptions.OSDFException;
import lombok.RequiredArgsConstructor;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;

import static io.osdf.common.utils.ReflectionUtils.hasAnnotation;
import static java.util.Arrays.stream;

@RequiredArgsConstructor
public class ApiCallFinder {
    private final Class<?> mainApiClass;

    public static ApiCallFinder finder(Class<?> apiClass) {
        return new ApiCallFinder(apiClass);
    }

    public ApiCall find(List<String> args) {
        return stream(mainApiClass.getMethods())
                .filter(method -> hasAnnotation(method, Import.class))
                .map(method -> resolve(method, args))
                .filter(Objects::nonNull)
                .findFirst()
                .orElseThrow(() -> new OSDFException("Method not found"));
    }

    private ApiCall resolve(Method method, List<String> fullArgs) {
        List<String> apiArgs = ImportPrefix.importPrefix(method).removePrefix(fullArgs);
        if (apiArgs.isEmpty()) return null;

        Class<?> apiClass = method.getAnnotation(Import.class).api();
        String methodName = apiArgs.get(0);
        List<String> methodArgs = apiArgs.subList(1, apiArgs.size());
        return createApiCall(apiClass, methodName, methodArgs);
    }

    private ApiCall createApiCall(Class<?> apiClass, String methodName, List<String> methodArgs) {
        try {
            return ApiCall.apiCall(apiClass, methodName, methodArgs);
        } catch (OSDFException e) {
            return null;
        }
    }
}
