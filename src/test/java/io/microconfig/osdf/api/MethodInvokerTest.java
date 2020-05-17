package io.microconfig.osdf.api;

import io.microconfig.osdf.exceptions.OSDFException;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static io.microconfig.osdf.api.MethodInvoker.methodInvoker;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MethodInvokerTest {
    void noExceptions() {
    }

    void throwNonOsdfException() {
        throw new RuntimeException();
    }

    void throwOsdfException() {
        throw new OSDFException();
    }

    @Test
    void noExceptionsCall() throws NoSuchMethodException {
        methodInvoker(method("noExceptions"), new MethodInvokerTest(), new Object[]{}).invoke();
    }

    @Test
    void nonOsdfExceptionCall() throws Exception {
        MethodInvoker invoker = methodInvoker(method("throwNonOsdfException"), new MethodInvokerTest(), new Object[]{});
        try {
            invoker.invoke();
        } catch (OSDFException e) {
            throw new Exception("OSDF exception is not expected");
        } catch (RuntimeException ignored) {
            return;
        }
        throw new Exception("Runtime exception should be thrown");
    }

    @Test
    void osdfExceptionCall() throws NoSuchMethodException {
        MethodInvoker invoker = methodInvoker(method("throwOsdfException"), new MethodInvokerTest(), new Object[]{});
        assertThrows(OSDFException.class, invoker::invoke);
    }

    private Method method(String name) throws NoSuchMethodException {
        return MethodInvokerTest.class.getDeclaredMethod(name);
    }


}