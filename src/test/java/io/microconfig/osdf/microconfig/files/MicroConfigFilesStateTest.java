package io.microconfig.osdf.microconfig.files;

import io.microconfig.osdf.paths.OSDFPaths;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;

import static io.microconfig.osdf.utils.CommandLineExecutor.execute;
import static java.nio.file.Files.exists;
import static java.nio.file.Files.writeString;
import static java.nio.file.Path.of;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MicroConfigFilesStateTest {
    private OSDFPaths paths;

    @BeforeEach
    void createConfigs() throws IOException {
        paths = null; //TODO
    }

    @Test
    void testOldFilesAndFoldersGetDeleted() throws IOException {
        execute("rm -rf " + null + "/repo/components/core/helloworld/helloworld-springboot/os.deploy"); //TODO
        writeString(Path.of(null + "/repo/components/core/helloworld/helloworld-springboot/os.deploy"), "some: value"); //TODO
//        new InitCommand(paths).run(null, null, null, null,
////                null, null, null, null,
////                null, null, null, null); TODO

        assertTrue(exists(of(paths.componentsPath() + "/helloworld-springboot/application.yaml")));
        assertFalse(exists(of(paths.componentsPath() + "/helloworld-springboot/openshift")));
    }
}