package io.microconfig.osdf.microconfig.properties;

import io.microconfig.osdf.utils.TestContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static io.microconfig.core.configtypes.StandardConfigType.PROCESS;
import static io.microconfig.osdf.microconfig.properties.PropertyGetter.propertyGetter;
import static io.microconfig.osdf.microconfig.properties.PropertySetter.propertySetter;
import static io.microconfig.osdf.utils.TestContext.defaultContext;
import static java.nio.file.Path.of;
import static org.junit.jupiter.api.Assertions.assertEquals;

class PropertyGetterSetterTest {
    private final TestContext context = defaultContext();

    @BeforeEach
    void createConfigs() throws IOException {
        context.initDev();
    }

    @Test
    void getSetGet() {
        assertEquals("latest", getCurrentVersion());
        propertySetter().setIfNecessary(of(context.getPaths().configsPath() + "/components/system/versions/project-version.proc"), "project.version", "v2");
        assertEquals("v2", getCurrentVersion());
    }

    private String getCurrentVersion() {
        return propertyGetter(context.getPaths()).get(PROCESS, "versions", "project.version");
    }
}