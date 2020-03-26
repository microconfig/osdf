package io.microconfig.osdf.components.loader;

import io.microconfig.osdf.components.AbstractOpenShiftComponent;
import io.microconfig.osdf.config.OSDFPaths;
import io.microconfig.osdf.utils.ConfigUnzipper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import static io.microconfig.osdf.components.loader.ComponentsLoaderImpl.componentsLoader;
import static io.microconfig.osdf.utils.InstallInitUtils.defaultInstallInit;
import static java.nio.file.Path.of;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ComponentsLoaderImplTest {
    private OSDFPaths paths;
    private Path configsPath = of("/tmp/configs");
    private Path osdfPath = of("/tmp/osdf");


    @BeforeEach
    void createConfigs() throws IOException {
        ConfigUnzipper.unzip("configs.zip", configsPath);
        paths = new OSDFPaths(osdfPath);
        defaultInstallInit(configsPath, osdfPath, paths);
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