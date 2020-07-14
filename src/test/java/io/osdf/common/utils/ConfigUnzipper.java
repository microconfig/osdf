package io.osdf.common.utils;

import lombok.RequiredArgsConstructor;

import java.nio.file.Path;

import static io.osdf.common.utils.CommandLineExecutor.execute;
import static java.nio.file.Files.exists;
import static java.util.Objects.requireNonNull;

@RequiredArgsConstructor
public class ConfigUnzipper {
    private final Path path;
    private final String resourceName;

    public static ConfigUnzipper configUnzipper(Path path, String resourceName) {
        return new ConfigUnzipper(path, resourceName);
    }

    public void unzip() {
        if (exists(path)) {
            execute("rm -rf " + path);
        }
        String dir = requireNonNull(ConfigUnzipper.class.getClassLoader().getResource(resourceName)).getPath();
        execute("cp -r " + dir + " " + path + "/configs/repo");
    }
}
