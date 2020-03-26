package io.microconfig.osdf.microconfig.files;

import io.microconfig.osdf.commands.InitCommand;
import io.microconfig.osdf.config.OSDFPaths;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;

import static io.microconfig.osdf.utils.CommandLineExecutor.execute;
import static io.microconfig.osdf.utils.InstallInitUtils.defaultInstallInit;
import static java.nio.file.Files.exists;
import static java.nio.file.Path.of;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MicroConfigFilesStateTest {
    private Path configsPath = of("/tmp/configs");
    private Path osdfPath = of("/tmp/osdf");

    @Test
    void testOldFilesAndFoldersGetDeleted() {
        OSDFPaths paths = new OSDFPaths(osdfPath);
        defaultInstallInit(configsPath, osdfPath, paths);
        execute("rm -rf " + configsPath + "/repo/components/core/helloworld-springboot/os.deploy");
        new InitCommand(paths).run(null, null, null, null,
                null, null, null, null,
                null, null, null, null);

        assertTrue(exists(of(paths.componentsPath() + "/helloworld-springboot/application.yaml")));
        assertFalse(exists(of(paths.componentsPath() + "/helloworld-springboot/openshift")));
    }
}