package io.osdf.api.lib;

import io.osdf.common.exceptions.StatusCodeException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.cli.ParseException;

import java.lang.reflect.Method;
import java.util.List;

import static io.osdf.api.lib.ApiArgsGetter.argsGetter;
import static io.osdf.api.lib.ApiReader.reader;
import static io.osdf.api.lib.MethodInvoker.methodInvoker;

@RequiredArgsConstructor
public class ApiCall {
    @Getter
    private final Class<?> apiClass;
    @Getter
    private final Method method;
    @Getter
    private final List<String> args;

    public static ApiCall apiCall(Class<?> apiClass, String methodName, List<String> args) {
        Method method = reader(apiClass).methodByName(methodName);
        return new ApiCall(apiClass, method, args);
    }

    public void invoke(Object implementation) {
        methodInvoker(method, implementation, methodArgs()).invoke();
    }

    private Object[] methodArgs() {
        try {
            return argsGetter(method, args.toArray(String[]::new)).get();
        } catch (ParseException e) {
            throw new StatusCodeException(1);
        }
    }
}
