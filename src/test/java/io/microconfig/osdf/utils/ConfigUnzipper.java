package io.microconfig.osdf.utils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

import static io.microconfig.osdf.utils.CommandLineExecutor.execute;
import static java.nio.file.Files.copy;
import static java.nio.file.Files.exists;
import static java.nio.file.Path.of;

public class ConfigUnzipper {
    public static void unzip(String configZipResource, Path unzipPath) throws IOException {
        if (exists(unzipPath)) {
            execute("rm -rf " + unzipPath);
        }
        InputStream resourceAsStream = ConfigUnzipper.class.getClassLoader().getResourceAsStream(configZipResource);
        if (resourceAsStream == null) throw new IOException("Couldn't read resource");
        copy(resourceAsStream, of("/tmp/configs.zip"));
        execute("unzip /tmp/configs.zip" + " -d " + unzipPath);
        execute("rm -rf /tmp/configs.zip");
    }
}
