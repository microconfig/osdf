package io.osdf.actions.init;

import io.osdf.actions.management.deploy.smart.image.RegistryCredentials;
import io.osdf.common.exceptions.OSDFException;
import io.osdf.context.TestContext;
import io.osdf.core.connection.cli.ClusterCli;
import io.osdf.core.connection.cli.openshift.OpenShiftCli;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

import static io.osdf.actions.init.InitializationApiImpl.initializationApi;
import static io.osdf.common.Credentials.of;
import static io.osdf.common.SettingsFile.settingsFile;
import static io.osdf.context.TestContext.defaultContext;
import static java.nio.file.Files.exists;
import static java.nio.file.Files.list;
import static java.util.Comparator.naturalOrder;
import static java.util.List.of;
import static java.util.stream.Collectors.toUnmodifiableList;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class InitializationApiImplTest {
    private static final TestContext context = defaultContext();
    private final ClusterCli cli = mock(ClusterCli.class);

    @BeforeEach
    void prepareEnv() {
        context.install();
        context.prepareConfigs();
    }

    @Test
    void initLocalConfigs() {
        initializationApi(context.getPaths(), cli).localConfigs(context.configsPath(), "master");
        assertTrue(exists(context.getPaths().configsPath()));
    }

    @Test
    void initRegistry() {
        initializationApi(context.getPaths(), cli).registry("example.com", of("user:pass"));
        RegistryCredentials settings = settingsFile(RegistryCredentials.class, context.getPaths().settings().registryCredentials())
                .getSettings();
        assertEquals(of("user:pass"), settings.getForUrl("example.com"));
    }

    @Test
    void exceptionIfPathIsNotProvided() {
        assertThrows(OSDFException.class, () -> initializationApi(context.getPaths(), mock(OpenShiftCli.class)).localConfigs(null, null));
    }

    @Test
    void buildIfEnvIsSet() throws IOException {
        initializationApi(context.getPaths(), cli).localConfigs(context.configsPath(), null);
        initializationApi(context.getPaths(), cli).configs("dev", null, null);
        try (Stream<Path> files = list(context.getPaths().componentsPath())) {
            List<String> builtComponents = files.map(Path::getFileName)
                    .map(Path::toString)
                    .sorted(naturalOrder())
                    .collect(toUnmodifiableList());
            assertEquals(of("simple-job", "simple-service"), builtComponents);
        }
    }

    @Test
    void exceptionIfEnvIsNotProvided() {
        initializationApi(context.getPaths(), cli).localConfigs(context.configsPath(), null);
        assertThrows(OSDFException.class, () -> initializationApi(context.getPaths(), cli).configs(null, null, null));
    }

    @Test
    void exceptionIfBuildWithoutConfigs() {
        assertThrows(OSDFException.class, () -> initializationApi(context.getPaths(), cli).configs("dev", null, null));
    }

    @Test
    void exceptionIfNoArgsForOpenShiftInit() {
        assertThrows(OSDFException.class, () -> initializationApi(context.getPaths(), cli).openshift(null, null, false));
    }

    @Test
    void exceptionIfBothArgsForOpenShiftInit() {
        assertThrows(OSDFException.class, () -> initializationApi(context.getPaths(), cli).openshift(of("user:pass"), "token", false));
    }

    @Test
    void initOpenShift() {
        initializationApi(context.getPaths(), cli).openshift(of("user:pass"), null, false);
        exists(context.getPaths().settings().openshift());

        initializationApi(context.getPaths(), cli).openshift(null, "token", false);
        exists(context.getPaths().settings().openshift());
    }
}