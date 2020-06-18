package io.microconfig.osdf.api;

import io.microconfig.osdf.api.annotation.Group;
import io.microconfig.osdf.exceptions.OSDFException;
import lombok.RequiredArgsConstructor;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;

import static io.microconfig.osdf.api.ImportPrefix.importPrefix;
import static io.microconfig.osdf.utils.ReflectionUtils.hasAnnotation;
import static java.util.Arrays.stream;

@RequiredArgsConstructor
public class ApiCallFinder {
    private final Class<?> mainApiClass;

    public static ApiCallFinder finder(Class<?> apiClass) {
        return new ApiCallFinder(apiClass);
    }

    public ApiCall find(List<String> args) {
        return stream(mainApiClass.getMethods())
                .filter(method -> hasAnnotation(method, Group.class))
                .map(method -> resolve(method, args))
                .filter(Objects::nonNull)
                .findFirst()
                .orElseThrow(() -> new OSDFException("Method not found"));
    }

    private ApiCall resolve(Method method, List<String> fullArgs) {
        List<String> apiArgs = importPrefix(method).removePrefix(fullArgs);
        if (apiArgs.isEmpty()) return null;

        Class<?> apiClass = method.getAnnotation(Group.class).api();
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
