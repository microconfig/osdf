package io.osdf.actions.system.install.migrations;

import io.osdf.context.TestContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.osdf.actions.system.install.migrations.AllMigrations.allMigrations;
import static io.osdf.context.TestContext.defaultContext;

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