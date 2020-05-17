package io.microconfig.osdf.utils;

import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

import static io.microconfig.osdf.utils.CommandLineExecutor.execute;
import static java.nio.file.Files.copy;
import static java.nio.file.Files.exists;
import static java.nio.file.Path.of;

@RequiredArgsConstructor
public class ConfigUnzipper {
    private final Path path;
    private final String resourceName;

    public static ConfigUnzipper configUnzipper(Path path, String resourceName) {
        return new ConfigUnzipper(path, resourceName);
    }

    public void unzip() throws IOException {
        if (exists(path)) {
            execute("rm -rf " + path);
        }
        if (!exists(of("/tmp/configs.zip"))) {
            InputStream resourceAsStream = ConfigUnzipper.class.getClassLoader().getResourceAsStream(resourceName);
            if (resourceAsStream == null) throw new IOException("Couldn't read resource " + resourceName);
            copy(resourceAsStream, of("/tmp/configs.zip"));
        }
        execute("unzip /tmp/configs.zip" + " -d " + path);
    }
}
