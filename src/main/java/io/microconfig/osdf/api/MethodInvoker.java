package io.microconfig.osdf.api;

import io.microconfig.osdf.exceptions.OSDFException;
import lombok.RequiredArgsConstructor;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@RequiredArgsConstructor
public class MethodInvoker {
    private final Method method;
    private final Object object;
    private final Object[] args;

    public static MethodInvoker methodInvoker(Method method, Object object, Object[] args) {
        return new MethodInvoker(method, object, args);
    }

    public void invoke() {
        try {
            method.invoke(object, args);
        } catch (IllegalAccessException | IllegalArgumentException e) {
            throw new OSDFException("Couldn't invoke method " + method.getName(), e);
        } catch (InvocationTargetException e) {
            if (e.getCause() instanceof OSDFException) {
                throw (OSDFException) e.getCause();
            } else {
                throw new RuntimeException("Exception in method " + method.getName(), e);
            }
        }
    }
}
