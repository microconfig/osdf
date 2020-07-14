package io.osdf.actions.system.install.jarinstaller;

import io.osdf.settings.version.OsdfVersion;
import io.osdf.context.TestContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static io.osdf.actions.system.install.jarinstaller.FakeJarInstaller.fakeJarInstaller;
import static io.osdf.settings.version.OsdfVersion.fromString;
import static io.osdf.actions.system.install.FileReplacerTester.fileReplacerTester;
import static io.osdf.context.TestContext.defaultContext;
import static java.nio.file.Path.of;
import static org.junit.jupiter.api.Assertions.assertEquals;


class FakeJarInstallerTest {
    private final TestContext context = defaultContext();

    @BeforeEach
    void installOsdf() {
        defaultContext().install();
    }

    @Test
    void successInstall() throws IOException {
        OsdfVersion version = fromString("1.0.1");
        FakeJarInstaller fakeJarInstaller = fakeJarInstaller(context.getPaths(), version);
        assertEquals(version, fakeJarInstaller.version());

        fileReplacerTester(fakeJarInstaller, of(context.getPaths().root() + "/osdf.jar")).replaceAndCheck();
    }
}