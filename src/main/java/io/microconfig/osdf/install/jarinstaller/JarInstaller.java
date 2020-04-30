package io.microconfig.osdf.install.jarinstaller;

import io.microconfig.osdf.install.FileReplacer;
import io.microconfig.osdf.state.OSDFVersion;

public interface JarInstaller extends FileReplacer {
    OSDFVersion version();
}
