package io.microconfig.osdf.install.migrations;

import io.microconfig.osdf.config.OSDFPaths;

import java.util.List;

import static io.microconfig.osdf.utils.CommandLineExecutor.execute;
import static java.util.List.of;

public class AllMigrations implements Migration {
    public static AllMigrations allMigrations() {
        return new AllMigrations();
    }

    @Override
    public void apply(OSDFPaths paths) {
        execute("cp " + paths.stateSavePath() + " " + paths.newStateSavePath());
        migrations().forEach(migration -> migration.apply(paths));
    }

    private List<Migration> migrations() {
        return of(new AddTokenMigration());
    }
}
