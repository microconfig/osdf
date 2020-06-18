package io.microconfig.osdf.install.migrations;

import io.osdf.settings.paths.OsdfPaths;

public interface Migration {
    void apply(OsdfPaths paths);
}
