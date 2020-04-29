package io.microconfig.osdf.components.loader;

import io.microconfig.osdf.components.AbstractOpenShiftComponent;
import io.microconfig.osdf.paths.OSDFPaths;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static io.microconfig.osdf.components.loader.ComponentsLoaderImpl.componentsLoader;
import static io.microconfig.osdf.utils.InstallInitUtils.createConfigsAndInstallInit;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ComponentsLoaderImplTest {
    private OSDFPaths paths;

    @BeforeEach
    void createConfigs() throws IOException {
        paths = createConfigsAndInstallInit();
    }

    @Test
    void loadAll() {
        List<AbstractOpenShiftComponent> components = componentsLoader(paths.componentsPath(), null, null).load();
        assertEquals(2, components.size());
    }

    @Test
    void loadOne() {
        List<AbstractOpenShiftComponent> components = componentsLoader(paths.componentsPath(), List.of("helloworld-springboot"), null).load();
        assertEquals(1, components.size());
    }

}