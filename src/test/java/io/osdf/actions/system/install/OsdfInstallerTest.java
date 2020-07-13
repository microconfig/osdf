package io.osdf.actions.system.install;

import io.osdf.settings.version.OsdfVersionFile;
import io.osdf.common.utils.TestContext;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;

import static io.osdf.actions.system.install.OsdfInstaller.osdfInstaller;
import static io.osdf.actions.system.install.jarinstaller.FakeJarInstaller.fakeJarInstaller;
import static io.osdf.common.SettingsFile.settingsFile;
import static io.osdf.settings.version.OsdfVersion.fromString;
import static io.osdf.common.utils.TestContext.defaultContext;
import static java.nio.file.Files.exists;
import static java.nio.file.Files.writeString;
import static java.nio.file.Path.of;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class OsdfInstallerTest {
    private final TestContext context = defaultContext();

    @Test
    void testFirstInstall() {
        context.clear();
        osdfInstaller(context.getPaths(), fakeJarInstaller(context.getPaths(), fromString("1.0.0")), false, true).install();
        checkVersion("1.0.0");
    }

    @Test
    void testReinstall() {
        context.install();
        new OsdfInstaller(context.getPaths(), fakeJarInstaller(context.getPaths(), fromString("1.0.1")), false, true, false).install();
        checkVersion("1.0.1");
    }

    @Test
    void testFullReinstall() throws IOException {
        context.install();
        Path indicator = of(context.getPaths().root() + "/indicator");
        writeString(indicator, "content");

        new OsdfInstaller(context.getPaths(), fakeJarInstaller(context.getPaths(), fromString("1.0.1")), true, true, false).install();
        checkVersion("1.0.1");
        assertFalse(exists(indicator));
    }

    private void checkVersion(String version) {
        String installedVersion = settingsFile(OsdfVersionFile.class, context.getPaths().settings().osdf()).getSettings().getVersion();
        assertEquals(version, installedVersion);
    }
}