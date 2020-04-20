package io.microconfig.osdf.microconfig.properties;

import io.microconfig.core.configtypes.ConfigType;
import io.microconfig.core.environments.Environment;
import io.microconfig.core.properties.Property;
import lombok.RequiredArgsConstructor;

import java.nio.file.Path;

import static io.microconfig.core.Microconfig.searchConfigsIn;
import static io.microconfig.core.configtypes.ConfigTypeFilters.configType;

@RequiredArgsConstructor
public class PropertyGetter {
    private final Environment environment;

    public static PropertyGetter propertyGetter(String env, Path configPath) {
        return new PropertyGetter(
                searchConfigsIn(configPath.toFile()).inEnvironment(env)
        );
    }

    public String get(ConfigType type, String component, String property) {
        return environment.getOrCreateComponentWithName(component)
                .getPropertiesFor(configType(type))
                .getPropertyWithKey(property)
                .map(Property::getValue)
                .orElseThrow(() -> new IllegalArgumentException("Can't resolve property " + component + "@" + property));
    }
}