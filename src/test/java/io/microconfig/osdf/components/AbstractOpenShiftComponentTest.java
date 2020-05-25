package io.microconfig.osdf.components;

import io.microconfig.osdf.openshift.OpenShiftCLI;
import io.microconfig.osdf.utils.TestContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;

import static io.microconfig.osdf.utils.TestContext.defaultContext;
import static io.microconfig.osdf.utils.mock.ApplyMock.applyMock;
import static io.microconfig.osdf.utils.mock.DeleteMock.deleteMock;
import static io.microconfig.osdf.utils.mock.OCMockAggregator.createMock;
import static io.microconfig.osdf.utils.mock.ResourceHashMock.resourceHashMock;
import static java.util.List.of;

class AbstractOpenShiftComponentTest {
    private final TestContext context = defaultContext();
    private OpenShiftCLI oc;

    @Test
    void testUpload() {
        component().upload();
    }

    @Test
    void testDelete() {
        component().delete();
    }

    @BeforeEach
    void setUp() throws IOException {
        context.initDev();
        oc = createMock(of(
                resourceHashMock(),
                applyMock(),
                deleteMock()
        ));
    }

    private DeploymentComponent component() {
        String name = "helloworld-springboot";
        String version = "latest";
        return new DeploymentComponent(name, version, Path.of(context.getPaths().componentsPath() + "/" + name), oc);
    }
}