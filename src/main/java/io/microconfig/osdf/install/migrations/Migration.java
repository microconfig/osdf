package io.microconfig.osdf.install.migrations;

import io.microconfig.osdf.config.OSDFPaths;

public interface Migration {
    void apply(OSDFPaths paths);
}
