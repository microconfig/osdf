package io.microconfig.osdf.install.migrations;

import io.osdf.settings.paths.OsdfPaths;

import java.util.List;

import static io.microconfig.osdf.install.migrations.CreateClusterContext.createClusterContext;
import static java.util.List.of;

public class AllMigrations implements Migration {
    public static AllMigrations allMigrations() {
        return new AllMigrations();
    }

    @Override
    public void apply(OsdfPaths paths) {
        migrations().forEach(migration -> migration.apply(paths));
    }

    private List<Migration> migrations() {
        return of(createClusterContext());
    }
}
