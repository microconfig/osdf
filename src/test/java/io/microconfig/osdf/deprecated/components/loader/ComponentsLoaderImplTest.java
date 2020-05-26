package io.microconfig.osdf.deprecated.components.loader;

import io.microconfig.osdf.deprecated.components.AbstractOpenShiftComponent;
import io.microconfig.osdf.utils.TestContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static io.microconfig.osdf.deprecated.components.loader.ComponentsLoaderImpl.componentsLoader;
import static io.microconfig.osdf.utils.TestContext.defaultContext;
import static java.util.List.of;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ComponentsLoaderImplTest {
    private final TestContext context = defaultContext();

    @BeforeEach
    void createConfigs() throws IOException {
        context.initDev();
    }

    @Test
    void loadAll() {
        List<AbstractOpenShiftComponent> components = componentsLoader(context.getPaths(), null, null).load();
        assertEquals(2, components.size());
    }

    @Test
    void loadOne() {
        List<AbstractOpenShiftComponent> components = componentsLoader(context.getPaths(), of("helloworld-springboot"), null).load(); //TODO
        assertEquals(1, components.size());
    }
}