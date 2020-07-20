package io.osdf.core.connection.cli;

import io.osdf.common.exceptions.OSDFException;
import io.osdf.test.cluster.TestCli;
import lombok.Getter;
import org.junit.jupiter.api.Test;

import static io.osdf.core.connection.cli.LoginCliProxy.loginCliProxy;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

class LoginCliProxyTest {
    @Test
    void checkExceptionIsSame() {
        TestInterface proxy = loginCliProxy(new TestClass(), mock(ClusterCli.class));
        assertThrows(OSDFException.class, proxy::throwOsdfException);
    }

    @Test
    void checkCliIsLoggedIn() {
        ProxyTestCli cli = new ProxyTestCli();
        TestInterface proxy = loginCliProxy(new TestClass(), cli);

        proxy.callWithoutErrors();

        assertTrue(cli.isLoggedIn());
    }

    interface TestInterface {
        void throwOsdfException();

        void callWithoutErrors();
    }

    private static class TestClass implements TestInterface {
        @Override
        public void throwOsdfException() {
            throw new OSDFException("Error");
        }

        @Override
        public void callWithoutErrors() {
            //no errors
        }
    }

    private static class ProxyTestCli extends TestCli {
        @Getter
        private boolean loggedIn = false;

        @Override
        public void login() {
            loggedIn = true;
        }
    }
}