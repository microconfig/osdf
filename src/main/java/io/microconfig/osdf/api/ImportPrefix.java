package io.microconfig.osdf.api;

import io.microconfig.osdf.api.annotation.Named;
import lombok.RequiredArgsConstructor;

import java.lang.reflect.Method;
import java.util.List;

import static io.microconfig.osdf.utils.ReflectionUtils.hasAnnotation;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;

@RequiredArgsConstructor
public class ImportPrefix {
    private final Method method;

    public static ImportPrefix importPrefix(Method method) {
        return new ImportPrefix(method);
    }

    public List<String> toList() {
        return asList(toString().split(" "));
    }

    public String toString() {
        Named annotation = method.getAnnotation(Named.class);
        if (annotation == null) return "";
        if (annotation.as().isEmpty()) return method.getName();
        return annotation.as();
    }

    public List<String> removePrefix(List<String> args) {
        if (!hasAnnotation(method, Named.class)) return args;
        List<String> prefix = toList();
        if (prefix.size() > args.size()) return emptyList();
        if (!prefix.equals(args.subList(0, prefix.size()))) return emptyList();
        return args.subList(prefix.size(), args.size());
    }
}
