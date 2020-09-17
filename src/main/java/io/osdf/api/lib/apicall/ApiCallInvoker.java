package io.osdf.api.lib.apicall;

import io.osdf.api.lib.definitions.MethodDefinition;
import io.osdf.common.exceptions.OSDFException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import static io.osdf.api.lib.argmappers.ArgMapper.argMapper;

public class ApiCallInvoker {
    public static ApiCallInvoker apiCallInvoker() {
        return new ApiCallInvoker();
    }

    public void invoke(ApiCall apiCall, Object target) {
        MethodDefinition methodDefinition = apiCall.getMethodDefinition();
        Method method = methodDefinition.getMethod();
        List<Object> args = argMapper().map(apiCall.getArgs(), apiCall.getMethodDefinition());
        doInvoke(method, target, args.toArray());
    }

    private void doInvoke(Method method, Object target, Object[] args) {
        try {
            method.invoke(target, args);
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
