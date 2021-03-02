package io.osdf.core.local.microconfig.state;

import io.osdf.core.connection.cli.ClusterCli;
import io.osdf.context.TestContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;

import static io.osdf.actions.init.InitializationApiImpl.initializationApi;
import static io.osdf.context.TestContext.defaultContext;
import static java.nio.file.Files.exists;
import static java.nio.file.Files.writeString;
import static java.nio.file.Path.of;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

class MicroConfigFilesStateTest {
    private final TestContext context = defaultContext();

    @BeforeEach
    void init() {
        context.initDev();
    }

    @Test
    void testOldFilesAndFoldersGetDeleted() throws IOException {
        Path pathToDeployConfig = of(context.getPaths().configsPath() + "/components/apps/simple-service/os.deploy");
        writeString(pathToDeployConfig, "some: value");
        initializationApi(context.getPaths(), mock(ClusterCli.class)).configs(null, null, null);

        assertTrue(exists(of(context.getPaths().componentsPath() + "/simple-service/application.yaml")));
        assertFalse(exists(of(context.getPaths().componentsPath() + "/simple-service/openshift")));
    }
}