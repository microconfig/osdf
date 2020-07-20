package io.osdf.core.connection.cli;

import io.osdf.common.exceptions.OSDFException;
import org.junit.jupiter.api.Test;

import static io.osdf.core.connection.cli.LoginCliProxy.loginCliProxy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class LoginCliProxyTest {
    interface TestInterface {
        void throwOsdfException();
    }

    private static class TestClass implements TestInterface {
        @Override
        public void throwOsdfException() {
            throw new OSDFException("Error");
        }
    }

    @Test
    void checkExceptionIsSame() {
        TestInterface proxy = loginCliProxy(new TestClass(), mock(ClusterCli.class));
        assertThrows(OSDFException.class, proxy::throwOsdfException);
    }
}