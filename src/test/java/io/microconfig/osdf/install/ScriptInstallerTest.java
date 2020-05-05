package io.microconfig.osdf.install;

import io.microconfig.osdf.utils.TestContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static io.microconfig.osdf.install.ScriptInstaller.scriptInstaller;
import static io.microconfig.osdf.utils.FileReplacerTester.fileReplacerTester;
import static io.microconfig.osdf.utils.TestContext.defaultContext;
import static java.nio.file.Path.of;

class ScriptInstallerTest {
    private final TestContext context = defaultContext();

    @BeforeEach
    void installOsdf() {
        defaultContext().install();
    }

    @Test
    void successInstall() throws IOException {
        ScriptInstaller installer = scriptInstaller(context.getPaths());
        fileReplacerTester(installer, of(context.getPaths().bin() + "/osdf")).replaceAndCheck();
    }
}