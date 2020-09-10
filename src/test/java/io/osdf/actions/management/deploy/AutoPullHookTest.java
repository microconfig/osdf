package io.osdf.actions.management.deploy;

import io.osdf.actions.init.configs.ConfigsUpdater;
import io.osdf.context.TestContext;
import io.osdf.core.connection.cli.ClusterCli;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.osdf.actions.init.configs.ConfigsUpdater.configsUpdater;
import static io.osdf.actions.management.deploy.AutoPullHook.autoPullHook;
import static io.osdf.common.utils.FileUtils.writeStringToFile;
import static io.osdf.context.TestContext.defaultContext;
import static java.nio.file.Files.exists;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class AutoPullHookTest {
    private static TestContext context = defaultContext();

    @BeforeEach
    void initConfigs() {
        context.clear();
        context.initDev();
    }

    @SneakyThrows
    @Test
    void doNotPull_ifNotEnabled() {
        writeStringToFile(context.configsPath().resolve("repo/test"), "test");

        autoPullHook(context.getPaths(), mock(ClusterCli.class)).tryAutoPull();

        assertFalse(exists(context.getPaths().configsPath().resolve("test")));
    }

    @Test
    void pull_ifEnabled() {
        writeStringToFile(context.configsPath().resolve("repo/test"), "test");

        new AutoPullHook(context.getPaths(), paths -> true, configsUpdater(context.getPaths(), mock(ClusterCli.class)))
                .tryAutoPull();

        assertTrue(exists(context.getPaths().configsPath().resolve("test")));
    }

    @Test
    void doNotPull_ifNoChanges() {
        ConfigsUpdater configsUpdater = spy(configsUpdater(context.getPaths(), mock(ClusterCli.class)));
        new AutoPullHook(context.getPaths(), paths -> true, configsUpdater).tryAutoPull();
        new AutoPullHook(context.getPaths(), paths -> true, configsUpdater).tryAutoPull();

        verify(configsUpdater, times(1)).fetch();
    }
}