package io.microconfig.osdf.openshift;

import io.microconfig.osdf.paths.OSDFPaths;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;

import static io.microconfig.osdf.openshift.OpenShiftResource.*;
import static io.microconfig.osdf.utils.InstallInitUtils.createConfigsAndInstallInit;
import static java.nio.file.Path.of;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class OpenShiftResourceTest {
    private OSDFPaths paths;

    @BeforeEach
    void createConfigs() throws IOException {
        paths = createConfigsAndInstallInit();
    }

    @Test
    void testFromOpenShiftNotation() throws NoSuchFieldException, IllegalAccessException {
        OpenShiftResource resource1 = fromOpenShiftNotation("kind/name", null);
        checkKindAndName(resource1, "kind", "name");

        OpenShiftResource resource2 = fromOpenShiftNotation("kind.long/name", null);
        checkKindAndName(resource2, "kind", "name");

        List<OpenShiftResource> resources = fromOpenShiftNotations(List.of("kind.long/name", "kind.long/name2", ""), null);
        assertEquals(2, resources.size());
        checkKindAndName(resources.get(0), "kind", "name");
        checkKindAndName(resources.get(1), "kind", "name2");
    }

    @Test
    void testFromPath() throws IllegalAccessException, NoSuchFieldException {
        OpenShiftResource resource = fromPath(of(paths.componentsPath() + "/helloworld-springboot/openshift/deployment.yaml"), null);
        checkKindAndName(resource, "deploymentconfig", "helloworld-springboot.latest");
    }

    @Test
    void testDelete() {
        OCExecutor oc = Mockito.mock(OCExecutor.class);
        when(oc.execute("oc delete kind name")).thenReturn("deleted");
        fromOpenShiftNotation("kind/name", oc).delete();
        verify(oc).execute("oc delete kind name");
    }

    private void checkKindAndName(OpenShiftResource resource, String actualKind, String actualName) throws NoSuchFieldException, IllegalAccessException {
        Field kind = resource.getClass().getDeclaredField("kind");
        kind.setAccessible(true);
        Field name = resource.getClass().getDeclaredField("name");
        name.setAccessible(true);
        assertEquals(actualKind, kind.get(resource));
        assertEquals(actualName, name.get(resource));
    }
}