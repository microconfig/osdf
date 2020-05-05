package io.microconfig.osdf.install.jarinstaller;

import io.microconfig.osdf.state.OSDFVersion;
import io.microconfig.osdf.utils.TestContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static io.microconfig.osdf.install.jarinstaller.FakeJarInstaller.fakeJarInstaller;
import static io.microconfig.osdf.state.OSDFVersion.fromString;
import static io.microconfig.osdf.utils.FileReplacerTester.fileReplacerTester;
import static io.microconfig.osdf.utils.TestContext.defaultContext;
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
        OSDFVersion version = fromString("1.0.1");
        FakeJarInstaller fakeJarInstaller = fakeJarInstaller(context.getPaths(), version);
        assertEquals(version, fakeJarInstaller.version());

        fileReplacerTester(fakeJarInstaller, of(context.getPaths().root() + "/osdf.jar")).replaceAndCheck();
    }
}