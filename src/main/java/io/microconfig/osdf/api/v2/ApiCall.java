package io.microconfig.osdf.api.v2;

import io.microconfig.osdf.exceptions.StatusCodeException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.cli.ParseException;

import java.lang.reflect.Method;
import java.util.List;

import static io.microconfig.osdf.api.ApiArgsGetter.argsGetter;
import static io.microconfig.osdf.api.v2.ApiReader.reader;
import static io.microconfig.osdf.api.v2.MethodInvoker.methodInvoker;

@RequiredArgsConstructor
public class ApiCall {
    @Getter
    private final Class<?> apiClass;
    private final Method method;
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
