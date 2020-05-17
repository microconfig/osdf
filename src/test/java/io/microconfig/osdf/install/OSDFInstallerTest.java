package io.microconfig.osdf.install;

import io.microconfig.osdf.state.OSDFVersionFile;
import io.microconfig.osdf.utils.TestContext;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;

import static io.microconfig.osdf.install.OSDFInstaller.osdfInstaller;
import static io.microconfig.osdf.install.jarinstaller.FakeJarInstaller.fakeJarInstaller;
import static io.microconfig.osdf.settings.SettingsFile.settingsFile;
import static io.microconfig.osdf.state.OSDFVersion.fromString;
import static io.microconfig.osdf.utils.TestContext.defaultContext;
import static java.nio.file.Files.exists;
import static java.nio.file.Files.writeString;
import static java.nio.file.Path.of;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class OSDFInstallerTest {
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
        new OSDFInstaller(context.getPaths(), fakeJarInstaller(context.getPaths(), fromString("1.0.1")), false, true, false).install();
        checkVersion("1.0.1");
    }

    @Test
    void testFullReinstall() throws IOException {
        context.install();
        Path indicator = of(context.getPaths().root() + "/indicator");
        writeString(indicator, "content");

        new OSDFInstaller(context.getPaths(), fakeJarInstaller(context.getPaths(), fromString("1.0.1")), true, true, false).install();
        checkVersion("1.0.1");
        assertFalse(exists(indicator));
    }

    private void checkVersion(String version) {
        String installedVersion = settingsFile(OSDFVersionFile.class, context.getPaths().settings().osdf()).getSettings().getVersion();
        assertEquals(version, installedVersion);
    }
}