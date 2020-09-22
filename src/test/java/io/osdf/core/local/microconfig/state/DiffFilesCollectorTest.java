package io.osdf.core.local.microconfig.state;

import io.osdf.context.TestContext;
import io.osdf.core.connection.cli.ClusterCli;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.List;

import static io.osdf.actions.configs.ConfigsApiImpl.configsApi;
import static io.osdf.common.utils.FileUtils.writeStringToFile;
import static io.osdf.context.TestContext.defaultContext;
import static io.osdf.core.local.microconfig.state.DiffFilesCollector.collector;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

class DiffFilesCollectorTest {
    private final TestContext context = defaultContext();

    @BeforeEach
    void initDev() {
        context.initDev();
    }

    @Test
    void testEmptyOnNewConfigs() {
        List<Path> changedFiles = collector(context.getPaths().root()).collect();
        assertEquals(0, changedFiles.size());
    }

    @Test
    void testCollectFor_application_and_deploy() {
        updateConfigsAndRebuild();

        List<Path> changedFiles = collector(context.getPaths().root()).collect();

        assertEquals(2, changedFiles.size());
    }

    private void updateConfigsAndRebuild() {
        addEntry("yaml");
        addEntry("deploy");
        configsApi(context.getPaths(), mock(ClusterCli.class)).pull();
    }

    private void addEntry(String ext) {
        writeStringToFile(context.configsPath().resolve("repo/components/apps/simple-service/add." + ext), "key: value");
    }

}