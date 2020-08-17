package io.osdf.common.utils;

import io.osdf.common.exceptions.OSDFException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Path;

import static io.osdf.common.utils.YamlUtils.loadFromPath;
import static java.nio.file.Files.writeString;
import static org.junit.jupiter.api.Assertions.assertThrows;

class YamlUtilsTest {
    @TempDir
    Path tempDir;

    @Test
    void ifFileIsNotValidYaml_throwOsdfException() throws IOException {
        Path yamlPath = tempDir.resolve("test.yaml");
        writeString(yamlPath, "a: b\n- c");
        assertThrows(OSDFException.class, () -> loadFromPath(yamlPath));
    }
}