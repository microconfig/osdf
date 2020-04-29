package io.microconfig.osdf.microconfig.files;

import io.microconfig.osdf.commands.InitCommand;
import io.microconfig.osdf.paths.OSDFPaths;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;

import static io.microconfig.osdf.utils.CommandLineExecutor.execute;
import static io.microconfig.osdf.utils.InstallInitUtils.DEFAULT_CONFIGS_PATH;
import static io.microconfig.osdf.utils.InstallInitUtils.createConfigsAndInstallInit;
import static java.nio.file.Files.exists;
import static java.nio.file.Files.writeString;
import static java.nio.file.Path.of;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MicroConfigFilesStateTest {
    private OSDFPaths paths;

    @BeforeEach
    void createConfigs() throws IOException {
        paths = createConfigsAndInstallInit();
    }

    @Test
    void testOldFilesAndFoldersGetDeleted() throws IOException {
        execute("rm -rf " + DEFAULT_CONFIGS_PATH + "/repo/components/core/helloworld/helloworld-springboot/os.deploy");
        writeString(Path.of(DEFAULT_CONFIGS_PATH + "/repo/components/core/helloworld/helloworld-springboot/os.deploy"), "some: value");
        new InitCommand(paths).run(null, null, null, null,
                null, null, null, null,
                null, null, null, null);

        assertTrue(exists(of(paths.componentsPath() + "/helloworld-springboot/application.yaml")));
        assertFalse(exists(of(paths.componentsPath() + "/helloworld-springboot/openshift")));
    }
}