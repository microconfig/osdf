package io.microconfig.osdf.api;

import io.microconfig.osdf.api.annotation.ApiCommand;
import io.microconfig.osdf.api.annotation.ConsoleParam;
import io.microconfig.osdf.api.annotation.Hidden;
import io.microconfig.osdf.exceptions.OSDFException;
import io.microconfig.osdf.parameters.ParamsContainer;
import io.microconfig.osdf.parameters.ParamsContainerBuilder;
import org.apache.commons.cli.ParseException;

import java.lang.reflect.Method;
import java.util.List;

import static io.microconfig.osdf.parameters.ParamsContainerBuilder.builder;
import static io.microconfig.osdf.utils.ReflectionUtils.processAnnotation;
import static io.microconfig.osdf.utils.StringUtils.pad;
import static io.microconfig.utils.Logger.announce;
import static java.util.Arrays.stream;
import static java.util.Comparator.comparingInt;
import static java.util.stream.Collectors.toUnmodifiableList;
import static java.util.stream.Stream.of;

public class OSDFApiInfo {
    public static List<String> commands() {
        return of(getOrderedMethods())
                .map(Method::getName)
                .collect(toUnmodifiableList());
    }

    public static Method methodByName(String name) {
        return of(OSDFApi.class.getMethods())
                .filter(method -> method.getName().equals(name))
                .findFirst()
                .orElseThrow(() -> new OSDFException("Unknown method " + name));
    }

    public static void printCommandInfos() {
        stream(getOrderedMethods()).forEach(method -> {
            String help = method.getAnnotation(ApiCommand.class).description();
            announce(pad(method.getName(), 50) + help);
        });
    }

    public static void printHelpForMethod(String methodName) {
        Method method = methodByName(methodName);
        ParamsContainerBuilder builder = builder(methodName);
        processAnnotation(method, ConsoleParam.class, param -> builder.add(param.value(), param.type()));
        builder.build().printHelp();
    }

    public static void printHelpForMethod(Method method, String fullMethodName) {
        ParamsContainerBuilder builder = builder(fullMethodName);
        processAnnotation(method, ConsoleParam.class, param -> builder.add(param.value(), param.type()));
        builder.build().printHelp();
    }

    public static ParamsContainer paramsFromAnnotations(String methodName, String[] args, List<ConsoleParam> annotations) throws ParseException {
        ParamsContainerBuilder builder = builder(methodName);
        annotations.forEach(param -> builder.add(param.value(), param.type()));
        return builder.build(args);
    }

    private static Method[] getOrderedMethods() {
        return stream(OSDFApi.class.getMethods())
                .filter(m -> m.getAnnotation(Hidden.class) == null)
                .sorted(comparingInt(OSDFApiInfo::orderOfMethod))
                .toArray(Method[]::new);
    }

    private static int orderOfMethod(Method method) {
        ApiCommand apiCommand = method.getAnnotation(ApiCommand.class);
        return apiCommand == null ? 100 : apiCommand.order();
    }
}
