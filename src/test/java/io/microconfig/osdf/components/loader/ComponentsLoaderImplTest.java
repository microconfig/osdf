package io.microconfig.osdf.components.loader;

import io.microconfig.osdf.components.AbstractOpenShiftComponent;
import io.microconfig.osdf.paths.OSDFPaths;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static io.microconfig.osdf.components.loader.ComponentsLoaderImpl.componentsLoader;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ComponentsLoaderImplTest {
    private OSDFPaths paths;

    @BeforeEach
    void createConfigs() throws IOException {
        paths = null; //TODO
    }

    @Test
    void loadAll() {
        List<AbstractOpenShiftComponent> components = componentsLoader(null, null, null).load(); //TODO
        assertEquals(2, components.size());
    }

    @Test
    void loadOne() {
        List<AbstractOpenShiftComponent> components = componentsLoader(null, List.of("helloworld-springboot"), null).load(); //TODO
        assertEquals(1, components.size());
    }

}