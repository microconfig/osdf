package io.microconfig.osdf.install.migrations;

import io.microconfig.osdf.utils.TestContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.microconfig.osdf.install.migrations.AllMigrations.allMigrations;
import static io.microconfig.osdf.utils.TestContext.defaultContext;

class AllMigrationsTest {
    private final TestContext context = defaultContext();

    @BeforeEach
    void installOsdf() {
        defaultContext().install();
    }

    @Test
    void successfulMigrations() {
        defaultContext().createDefaultConfigs();
        allMigrations().apply(context.getPaths());
    }

}