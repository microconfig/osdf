package io.microconfig.osdf.api.implementations;

import io.microconfig.osdf.cluster.cli.ClusterCLI;
import io.microconfig.osdf.cluster.openshift.OpenShiftCLI;
import io.microconfig.osdf.common.Credentials;
import io.microconfig.osdf.exceptions.OSDFException;
import io.microconfig.osdf.utils.TestContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

import static io.microconfig.osdf.api.implementations.InitializationApiImpl.initializationApi;
import static io.microconfig.osdf.utils.TestContext.CONFIGS_PATH;
import static io.microconfig.osdf.utils.TestContext.defaultContext;
import static java.nio.file.Files.exists;
import static java.nio.file.Files.list;
import static java.util.Comparator.naturalOrder;
import static java.util.List.of;
import static java.util.stream.Collectors.toUnmodifiableList;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class InitializationApiImplTest {
    private final static TestContext context = defaultContext();
    private final ClusterCLI cli = mock(ClusterCLI.class);

    @BeforeEach
    void prepareEnv() throws IOException {
        context.install();
        context.prepareConfigs();
    }

    @Test
    void initLocalConfigs() {
        initializationApi(context.getPaths(), cli).localConfigs(CONFIGS_PATH, null);
        assertTrue(exists(context.getPaths().configsPath()));
    }

    @Test
    void exceptionIfPathIsNotProvided() {
        assertThrows(OSDFException.class, () -> initializationApi(context.getPaths(), mock(OpenShiftCLI.class)).localConfigs(null, null));
    }

    @Test
    void buildIfEnvIsSet() throws IOException {
        initializationApi(context.getPaths(), cli).localConfigs(CONFIGS_PATH, null);
        initializationApi(context.getPaths(), cli).configs("dev", null);
        try (Stream<Path> files = list(context.getPaths().componentsPath())) {
            List<String> builtComponents = files.map(Path::getFileName)
                    .map(Path::toString)
                    .sorted(naturalOrder())
                    .collect(toUnmodifiableList());
            assertEquals(of("fakejob", "helloworld-springboot"), builtComponents);
        }
    }

    @Test
    void exceptionIfEnvIsNotProvided() {
        initializationApi(context.getPaths(), cli).localConfigs(CONFIGS_PATH, null);
        assertThrows(OSDFException.class, () -> initializationApi(context.getPaths(), cli).configs(null, null));
    }

    @Test
    void exceptionIfBuildWithoutConfigs() {
        assertThrows(OSDFException.class, () -> initializationApi(context.getPaths(), cli).configs("dev", null));
    }

    @Test
    void exceptionIfNoArgsForOpenShiftInit() {
        assertThrows(OSDFException.class, () -> initializationApi(context.getPaths(), cli).openshift(null, null, false));
    }

    @Test
    void exceptionIfBothArgsForOpenShiftInit() {
        assertThrows(OSDFException.class, () -> initializationApi(context.getPaths(), cli).openshift(Credentials.of("user:pass"), "token", false));
    }

    @Test
    void initOpenShift() {
        initializationApi(context.getPaths(), cli).openshift(Credentials.of("user:pass"), null, false);
        exists(context.getPaths().settings().openshift());

        initializationApi(context.getPaths(), cli).openshift(null, "token", false);
        exists(context.getPaths().settings().openshift());
    }
}