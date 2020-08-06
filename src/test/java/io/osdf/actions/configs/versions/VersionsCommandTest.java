package io.osdf.actions.configs.versions;

import io.osdf.common.yaml.YamlObject;
import io.osdf.context.TestContext;
import io.osdf.core.connection.cli.ClusterCli;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.osdf.actions.configs.versions.VersionsCommand.versionsCommand;
import static io.osdf.context.TestContext.defaultContext;
import static io.osdf.core.application.core.AllApplications.all;
import static io.osdf.core.application.core.files.loaders.ApplicationFilesLoaderImpl.appLoader;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.Mockito.mock;

class VersionsCommandTest {
    private final TestContext context = defaultContext();

    @BeforeEach
    void createConfigs() {
        context.initDev();
    }

    @Test
    void testChangeVersions_forAllApps() {
        versionsCommand(context.getPaths(), mock(ClusterCli.class))
                .setVersions("cv", "pv", null);

        YamlObject appProperties = getAppProperties("simple-service");

        assertEquals("pv", appProperties.get("app.version"));
        assertEquals("cv", appProperties.get("config.version"));
    }

    @Test
    void testChangeVersions_forSingleApp() {
        versionsCommand(context.getPaths(), mock(ClusterCli.class))
                .setVersions("cv", "pv", "simple-service");

        YamlObject appProperties = getAppProperties("simple-service");
        YamlObject otherAppProperties = getAppProperties("simple-job");

        assertEquals("pv", appProperties.get("app.version"));
        assertEquals("cv", appProperties.get("config.version"));

        assertNotEquals("pv", otherAppProperties.get("app.version"));
        assertNotEquals("cv", otherAppProperties.get("config.version"));
    }

    private YamlObject getAppProperties(String name) {
        return appLoader(context.getPaths())
                .load(all(mock(ClusterCli.class))).stream()
                .filter(app -> app.name().equals(name))
                .findFirst()
                .orElseThrow()
                .files()
                .deployProperties();
    }
}