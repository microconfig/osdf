package io.osdf.test.local;

import io.osdf.core.application.core.files.ApplicationFiles;
import io.osdf.core.local.component.MicroConfigComponentDir;

import java.io.IOException;
import java.nio.file.Path;

import static io.osdf.actions.init.configs.postprocess.metadata.MetadataCreatorImpl.metadataCreator;
import static io.osdf.core.application.core.files.ApplicationFilesImpl.applicationFiles;
import static io.osdf.core.local.component.MicroConfigComponentDir.componentDir;
import static io.osdf.test.ClasspathReader.classpathFile;
import static java.nio.file.Files.createTempDirectory;
import static org.apache.commons.io.FileUtils.copyDirectory;

public class AppUtils {
    public static ApplicationFiles applicationFilesFor(String name) {
        return applicationFilesFor(name, "");
    }

    public static ApplicationFiles applicationFilesFor(String name, String subPath) {
        Path serviceDir = classpathFile("components/" + name);
        MicroConfigComponentDir componentDir;
        try {
            Path destination = Path.of(createTempDirectory(name) + subPath);
            copyDirectory(serviceDir.toFile(), destination.toFile());
            componentDir = componentDir(destination);
        } catch (IOException e) {
            throw new RuntimeException("Couldn't copy component " + name, e);
        }

        ApplicationFiles files = applicationFiles(componentDir);
        metadataCreator().create(componentDir);
        return files;
    }
}
