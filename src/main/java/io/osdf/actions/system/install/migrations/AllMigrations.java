package io.osdf.actions.system.install.migrations;

import io.osdf.settings.paths.OsdfPaths;

import java.util.List;

import static io.osdf.actions.system.install.migrations.CreateClusterContext.createClusterContext;
import static io.osdf.actions.system.install.migrations.EncryptionMigration.encryptionMigration;
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
        return of(createClusterContext(), encryptionMigration());
    }
}
