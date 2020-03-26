package io.microconfig.osdf.microconfig.properties;

import io.microconfig.core.properties.ConfigProvider;
import io.microconfig.factory.MicroconfigFactory;
import io.microconfig.factory.configtypes.StandardConfigTypes;
import lombok.RequiredArgsConstructor;

import java.nio.file.Path;
import java.util.Map;

import static io.microconfig.core.environments.Component.byType;
import static io.microconfig.factory.MicroconfigFactory.init;
import static io.microconfig.factory.configtypes.StandardConfigTypes.*;
import static java.util.Map.of;

@RequiredArgsConstructor
public class PropertyGetter {
    private final String env;
    private final Map<StandardConfigTypes, ConfigProvider> providers;

    public static PropertyGetter propertyGetter(String env, Path configPath) {
        MicroconfigFactory factory = init(configPath.toFile(), null);
        return new PropertyGetter(env, of(
                PROCESS, provider(factory, PROCESS),
                DEPLOY, provider(factory, DEPLOY),
                APPLICATION, provider(factory, APPLICATION)
        ));
    }

    private static ConfigProvider provider(MicroconfigFactory factory, StandardConfigTypes type) {
        return factory.newConfigProvider(type.getType());
    }

    public String get(StandardConfigTypes type, String component, String property) {
        return providers.get(type).getProperties(byType(component), env).get(property).getValue();
    }
}
