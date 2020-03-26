package io.microconfig.osdf.api;

import lombok.RequiredArgsConstructor;
import org.apache.commons.cli.ParseException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static io.microconfig.osdf.api.ApiArgsGetter.argsGetter;
import static io.microconfig.osdf.api.OSDFApiInfo.methodByName;
import static java.lang.System.exit;

@RequiredArgsConstructor
public class ApiCaller {
    private final ConsoleArgsProducer consoleArgsProducer;

    public static ApiCaller apiCaller(ConsoleArgsProducer consoleArgsProducer) {
        return new ApiCaller(consoleArgsProducer);
    }

    public void callCommand(OSDFApi api, String name) {
        Method method = methodByName(name);
        invoke(api, name, method, getArgs(method));
    }

    private Object[] getArgs(Method method) {
        try {
            return argsGetter(method, consoleArgsProducer.args()).get();
        } catch (ParseException e) {
            exit(1);
            return null;
        }
    }

    private void invoke(OSDFApi api, String name, Method method, Object[] args) {
        try {
            method.invoke(api, args);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException("Couldn't invoke method " + name, e);
        }
    }
}
