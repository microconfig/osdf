package io.osdf.actions.init.configs.postprocess;

import io.osdf.actions.init.InitializationApiImpl;
import io.osdf.actions.init.configs.postprocess.ExternalVariablesResolver;
import io.osdf.common.exceptions.OSDFException;
import io.osdf.context.TestContext;
import io.osdf.core.connection.cli.ClusterCli;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;

import static io.osdf.actions.init.InitializationApiImpl.initializationApi;
import static io.osdf.actions.init.configs.postprocess.ExternalVariablesResolver.externalVariablesResolver;
import static io.osdf.common.utils.FileUtils.readAll;
import static io.osdf.common.utils.FileUtils.writeStringToFile;
import static io.osdf.common.yaml.YamlObject.yaml;
import static io.osdf.context.TestContext.defaultContext;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class ExternalVariablesResolverTest {
    @TempDir
    Path tempDir;

    @Test
    void success() {
        assertResolver(resolver(), "" +
                "integer: ~external(integer.key)" + "\n" +
                "string: ~external(string.key)" + "\n" +
                "concat: ~external(integer.key)~external(string.key)", "" +

                "integer: 1" + "\n" +
                "string: test" + "\n" +
                "concat: 1test");
    }

    @Test
    void defaultValue() {
        assertResolver(resolver(), "" +
                "string: ~external(unknown.key, default)", "" +

                "string: default");
    }

    @Test
    void throwOsdfException_ifNoKey_and_noDefault() {
        assertThrows(OSDFException.class, () ->
                assertResolver(resolver(), "" +
                        "string: ~external(unknown.key)", "" +

                        "string: ?"));
    }

    @Test
    void noErrors_ifExternalFileIsNotConfigured() {
        TestContext context = defaultContext();
        context.initDev();
        assertResolver(externalVariablesResolver(context.getPaths()),
                "this: ~external(other, default)", "this: default");
    }

    @Test
    void noErrors_ifExternalFileIsNotFound() {
        TestContext context = defaultContext();
        context.initDev();
        initializationApi(context.getPaths(), mock(ClusterCli.class)).configs(null, null, "unknown/file");
        assertResolver(externalVariablesResolver(context.getPaths()),
                "this: ~external(other, default)", "this: default");
    }

    private void assertResolver(ExternalVariablesResolver resolver, String from, String to) {
        Path file = tempDir.resolve("file.yaml");
        writeStringToFile(file, from);
        resolver.resolve(tempDir);
        assertEquals(to, readAll(file));
    }

    private ExternalVariablesResolver resolver() {
        return new ExternalVariablesResolver(yaml("" +
                "integer:" + "\n" +
                "  key: 1" + "\n" +
                "string:" + "\n" +
                "  key: test"
        ));
    }
}