package io.microconfig.osdf.install;

import io.microconfig.osdf.utils.TestContext;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;

import static io.microconfig.osdf.install.WorkfolderInstaller.workfolderInstaller;
import static io.microconfig.osdf.utils.TestContext.defaultContext;
import static java.nio.file.Files.exists;
import static java.nio.file.Files.writeString;
import static java.nio.file.Path.of;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class WorkfolderInstallerTest {
    private final TestContext context = defaultContext();
    private final WorkfolderInstaller installer = workfolderInstaller(context.getPaths());

    @Test
    void createNewFolder() {
        context.clear();
        installer.install(false);
        assertTrue(exists(context.getPaths().root()));
    }

    @Test
    void testFullReinstallOption() throws IOException {
        context.install();
        Path indicator = of(context.getPaths().root() + "/indicator");
        writeString(indicator, "content");

        installer.install(false);
        assertTrue(exists(indicator));

        installer.install(true);
        assertFalse(exists(indicator));
    }
}