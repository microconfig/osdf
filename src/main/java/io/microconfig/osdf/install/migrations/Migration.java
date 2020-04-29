package io.microconfig.osdf.install.migrations;

import io.microconfig.osdf.paths.OSDFPaths;

public interface Migration {
    void apply(OSDFPaths paths);
}
