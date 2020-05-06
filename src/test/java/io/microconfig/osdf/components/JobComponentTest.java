package io.microconfig.osdf.components;

import io.microconfig.osdf.openshift.OCExecutor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class JobComponentTest {
    private OCExecutor oc;
    private JobComponent component;
    private final Map<String, String> commands = new HashMap<>();

    @BeforeEach
    void setUp() {
        oc = mock(OCExecutor.class);
        component = new JobComponent("test-name", null, Path.of("/tmp/components/test-name/openshift"), oc);

        commands.put("exists", "oc get job test-name");
    }

    @Test
    void testExists() {
//        when(oc.execute(commands.get("exists"), true)).thenReturn("exists");
        assertTrue(component.exists());
//        verify(oc).execute(commands.get("exists"), true);
    }

    @Test
    void testNotExists() {
//        when(oc.execute(commands.get("exists"), true)).thenReturn("error");
        assertFalse(component.exists());
//        verify(oc).execute(commands.get("exists"), true);
    }
}