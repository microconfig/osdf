package io.microconfig.osdf.api.v2;

import io.microconfig.osdf.api.annotation.Import;
import io.microconfig.osdf.api.annotation.Named;
import io.microconfig.osdf.exceptions.OSDFException;
import lombok.RequiredArgsConstructor;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;

import static io.microconfig.osdf.utils.ReflectionUtils.hasAnnotation;
import static java.util.Arrays.asList;
import static java.util.Arrays.stream;
import static java.util.List.of;

@RequiredArgsConstructor
public class ApiFinder {
    private final Class<?> apiClass;

    public static ApiFinder finder(Class<?> apiClass) {
        return new ApiFinder(apiClass);
    }

    public ApiCall apiCall(List<String> args) {
        return stream(apiClass.getMethods())
                .filter(method -> hasAnnotation(method, Import.class))
                .map(method -> resolve(method, args))
                .filter(Objects::nonNull)
                .findFirst()
                .orElseThrow(() -> new OSDFException("Method not found"));
    }

    private ApiCall resolve(Method method, List<String> fullArgs) {
        List<String> apiArgs = removePrefix(method, fullArgs);
        if (apiArgs == null) return null;
        if (apiArgs.isEmpty()) throw new OSDFException("Specify command name");

        Class<?> apiClass = method.getAnnotation(Import.class).value();
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

    private List<String> removePrefix(Method method, List<String> fullArgs) {
        if (!hasAnnotation(method, Named.class)) return fullArgs;
        List<String> prefix = getPrefix(method);
        if (prefix.size() > fullArgs.size()) return null;
        if (!prefix.equals(fullArgs.subList(0, prefix.size()))) return null;
        return fullArgs.subList(prefix.size(), fullArgs.size());
    }

    private List<String> getPrefix(Method method) {
        Named annotation = method.getAnnotation(Named.class);
        if (annotation.as().isEmpty()) return of(method.getName());
        return asList(annotation.as().split(" "));
    }
}
