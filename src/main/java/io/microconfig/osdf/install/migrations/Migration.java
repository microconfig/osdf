package io.microconfig.osdf.install.migrations;

import io.osdf.settings.paths.OSDFPaths;

public interface Migration {
    void apply(OSDFPaths paths);
}
