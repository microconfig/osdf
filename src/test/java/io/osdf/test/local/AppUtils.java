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
        Path serviceDir = classpathFile("components/" + name);
        MicroConfigComponentDir componentDir = null;
        try {
            Path tempDir = createTempDirectory(name);
            copyDirectory(serviceDir.toFile(), tempDir.toFile());
            componentDir = componentDir(tempDir);
        } catch (IOException e) {
            throw new RuntimeException("Couldn't copy component " + name, e);
        }

        ApplicationFiles files = applicationFiles(componentDir);
        metadataCreator().create(componentDir);
        return files;
    }
}
