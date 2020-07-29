package io.osdf.actions.system.install.jarinstaller;

import io.osdf.actions.system.install.FileReplacer;
import io.osdf.settings.version.OsdfVersion;

public interface JarInstaller extends FileReplacer {
    String JAR_NAME = "osdf.jar";

    OsdfVersion version();
}
