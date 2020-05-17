package io.microconfig.osdf.microconfig.files;

import io.microconfig.osdf.utils.TestContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;

import static io.microconfig.osdf.api.implementations.InitializationApiImpl.initializationApi;
import static io.microconfig.osdf.utils.TestContext.defaultContext;
import static java.nio.file.Files.exists;
import static java.nio.file.Files.writeString;
import static java.nio.file.Path.of;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MicroConfigFilesStateTest {
    private final TestContext context = defaultContext();

    @BeforeEach
    void init() throws IOException {
        context.initDev();
    }

    @Test
    void testOldFilesAndFoldersGetDeleted() throws IOException {
        Path pathToDeployConfig = of(context.getPaths().configsPath() + "/components/core/openshift/helloworld-springboot/os.deploy");
        writeString(pathToDeployConfig, "some: value");
        initializationApi(context.getPaths()).configs(null, null);

        assertTrue(exists(of(context.getPaths().componentsPath() + "/helloworld-springboot/application.yaml")));
        assertFalse(exists(of(context.getPaths().componentsPath() + "/helloworld-springboot/openshift")));
    }
}