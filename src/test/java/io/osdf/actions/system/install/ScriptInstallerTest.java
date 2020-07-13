package io.osdf.actions.system.install;

import io.osdf.common.utils.TestContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static io.osdf.actions.system.install.ScriptInstaller.scriptInstaller;
import static io.osdf.common.utils.FileReplacerTester.fileReplacerTester;
import static io.osdf.common.utils.TestContext.defaultContext;
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