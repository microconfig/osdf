package io.microconfig.osdf.install.migrations;

import io.microconfig.osdf.paths.OSDFPaths;

import java.util.List;

import static java.util.List.of;

public class AllMigrations implements Migration {
    public static AllMigrations allMigrations() {
        return new AllMigrations();
    }

    @Override
    public void apply(OSDFPaths paths) {
        migrations().forEach(migration -> migration.apply(paths));
    }

    private List<Migration> migrations() {
        return of();
    }
}
