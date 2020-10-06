package io.osdf.actions.init.configs.postprocess;

import io.osdf.common.yaml.YamlObject;
import io.osdf.core.local.component.ComponentDir;
import org.junit.jupiter.api.Test;

import static io.osdf.actions.init.configs.postprocess.AppPostProcessor.componentPostProcessor;
import static io.osdf.common.yaml.YamlObject.yaml;
import static io.osdf.test.local.AppUtils.componentDirFor;
import static java.nio.file.Files.exists;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AppPostProcessorTest {
    @Test
    void testConfigMapIsCreated_WithoutResources() {
        ComponentDir componentDir = componentDirFor("configmap-plainApp");

        componentPostProcessor().process(componentDir);

        assertTrue(exists(componentDir.getPath("resources/configmap-app-configmap.yaml")));
    }

    @Test
    void testResourceTemplatePostProcessor() {
        ComponentDir componentDir = componentDirFor("with-template-engine-usage");

        componentPostProcessor().process(componentDir);

        YamlObject yaml = yaml(componentDir.getPath("resources/deployment.yaml"));
        assertNotNull(yaml.get("data.key"));
    }
}