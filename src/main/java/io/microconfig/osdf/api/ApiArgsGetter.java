package io.microconfig.osdf.api;

import io.microconfig.osdf.api.annotation.ConsoleParam;
import io.microconfig.osdf.parameters.ParamsContainer;
import lombok.RequiredArgsConstructor;
import org.apache.commons.cli.ParseException;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static io.microconfig.osdf.api.OSDFApiInfo.paramsFromAnnotations;
import static io.microconfig.osdf.utils.ReflectionUtils.annotations;
import static java.util.Arrays.asList;
import static java.util.Arrays.copyOfRange;

@RequiredArgsConstructor
public class ApiArgsGetter {
    private final Method method;
    private final String[] args;

    public static ApiArgsGetter argsGetter(Method method, String[] args) {
        return new ApiArgsGetter(method, args);
    }

    public Object[] get() throws ParseException {
        int splitIndex = plainCallSplitIndex(args);
        String[] plainCallArgs = copyOfRange(args, 0, splitIndex);
        String[] argsWithFlag = copyOfRange(args, splitIndex, args.length);

        List<ConsoleParam> annotations = annotations(method, ConsoleParam.class);
        Class<?>[] parameterTypes = method.getParameterTypes();
        List<Object> result = plainCallArgs(plainCallArgs, parameterTypes);

        List<ConsoleParam> annotationsNotProcessed = annotations.subList(result.size(), annotations.size());
        ParamsContainer params = paramsFromAnnotations(method.getName(), argsWithFlag, annotationsNotProcessed);
        annotationsNotProcessed.forEach(param -> result.add(params.get(param.value())));

        return result.toArray();
    }

    private List<Object> plainCallArgs(String[] args, Class<?>[] parameterTypes) {
        int ind = 0;
        List<Object> result = new ArrayList<>();
        for (Class<?> type : parameterTypes) {
            if (ind >= args.length) return result;
            if (String.class.isAssignableFrom(type)) {
                result.add(args[ind]);
                ind++;
            } else if (List.class.isAssignableFrom(type)) {
                result.add(asList(copyOfRange(args, ind, args.length)));
                ind = args.length;
            } else {
                result.add(args[ind]);
                ind = args.length;
            }
        }
        return result;
    }

    private int plainCallSplitIndex(String[] args) {
        for (int i = 0; i < args.length; i++) {
            if (args[i].startsWith("-")) return i;
        }
        return args.length;
    }
}
