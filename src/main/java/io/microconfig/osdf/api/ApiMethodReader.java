package io.microconfig.osdf.api;

import io.microconfig.osdf.api.annotation.ApiCommand;
import io.microconfig.osdf.api.annotation.ConsoleParam;
import io.microconfig.osdf.parameters.ParamsContainerBuilder;
import lombok.RequiredArgsConstructor;

import java.lang.reflect.Method;

import static io.microconfig.osdf.parameters.ParamsContainerBuilder.builder;
import static io.microconfig.osdf.utils.ReflectionUtils.processAnnotation;
import static io.microconfig.utils.Logger.info;

@RequiredArgsConstructor
public class ApiMethodReader {
    private final Method method;
    private final String fullName;

    public static ApiMethodReader apiMethodReader(Method method, String fullName) {
        return new ApiMethodReader(method, fullName);
    }

    public static ApiMethodReader apiMethodReader(Method method) {
        return new ApiMethodReader(method, method.getName());
    }

    public void printHelp() {
        info(description());
        printInfo();
    }

    public String description() {
        return method.getAnnotation(ApiCommand.class).description();
    }

    private void printInfo() {
        ParamsContainerBuilder builder = builder(fullName);
        processAnnotation(method, ConsoleParam.class, param -> builder.add(param.value(), param.type()));
        builder.build().printHelp();
    }
}
