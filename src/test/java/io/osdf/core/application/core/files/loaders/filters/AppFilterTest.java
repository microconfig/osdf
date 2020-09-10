package io.osdf.core.application.core.files.loaders.filters;

import io.osdf.core.local.component.ComponentDir;
import org.junit.jupiter.api.Test;

import static io.osdf.core.application.core.files.loaders.filters.AppFilter.isApp;
import static io.osdf.test.local.AppUtils.componentDirFor;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AppFilterTest {
    @Test
    void isAppIf_DeployYaml_And_resources() {
        ComponentDir componentDir = componentDirFor("simple-service");

        assertTrue(isApp().test(componentDir));
    }

    @Test
    void isAppIf_DeployYaml_And_osdfConfigMapProperty() {
        ComponentDir componentDir = componentDirFor("configmap-plainApp");

        assertTrue(isApp().test(componentDir));
    }
}