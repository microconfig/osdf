package io.osdf.core.connection.cli;

import io.osdf.common.exceptions.OSDFException;
import lombok.RequiredArgsConstructor;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static java.lang.ClassLoader.getSystemClassLoader;
import static java.lang.reflect.Proxy.newProxyInstance;

@RequiredArgsConstructor
public class LoginCliProxy implements InvocationHandler {
    private final Object delegate;
    private final ClusterCli cli;

    @SuppressWarnings("unchecked")
    public static <T> T loginCliProxy(T delegate, ClusterCli cli) {
        return (T) newProxyInstance(getSystemClassLoader(),
                delegate.getClass().getInterfaces(),
                new LoginCliProxy(delegate, cli));
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        cli.login();
        try {
            return method.invoke(delegate, args);
        } catch (InvocationTargetException e) {
            if (e.getCause() instanceof OSDFException) throw e.getCause();
            throw e;
        }
    }
}
