package io.osdf.common.utils;

import io.osdf.common.exceptions.PossibleBugException;
import org.junit.jupiter.api.Test;

import static io.osdf.common.utils.CommandLineExecutor.execute;
import static java.util.List.of;
import static org.junit.jupiter.api.Assertions.assertFalse;

class CommandLineExecutorTest {
    @Test
    void testCredentialsHiding() {
        try {
            execute("unknown command user:password", of("user", "password"));
        } catch (PossibleBugException e) {
            assertFalse(e.getMessage().contains("user"));
            assertFalse(e.getMessage().contains("password"));
        }
    }
}