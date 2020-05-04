package io.microconfig.osdf.microconfig.properties;

import io.microconfig.core.Microconfig;
import io.microconfig.core.configtypes.ConfigType;
import io.microconfig.core.environments.Environment;
import io.microconfig.core.properties.Property;
import io.microconfig.core.properties.Resolver;
import io.microconfig.osdf.configs.ConfigsSettings;
import io.microconfig.osdf.exceptions.MicroConfigException;
import io.microconfig.osdf.paths.OSDFPaths;
import lombok.RequiredArgsConstructor;

import static io.microconfig.core.Microconfig.searchConfigsIn;
import static io.microconfig.core.configtypes.ConfigTypeFilters.configType;
import static io.microconfig.osdf.settings.SettingsFile.settingsFile;

@RequiredArgsConstructor
public class PropertyGetter {
    private final Environment environment;
    private final Resolver resolver;

    public static PropertyGetter propertyGetter(OSDFPaths paths) {
        String env = settingsFile(ConfigsSettings.class, paths.settings().configs())
                .getSettings()
                .getEnv();

        try {
            Microconfig microconfig = searchConfigsIn(paths.configsPath().toFile());
            return new PropertyGetter(
                    microconfig.inEnvironment(env),
                    microconfig.resolver()
            );
        } catch (RuntimeException e) {
            throw new MicroConfigException();
        }
    }

    public String get(ConfigType type, String component, String property) {
        return environment.getOrCreateComponentWithName(component)
                .getPropertiesFor(configType(type))
                .resolveBy(resolver)
                .getPropertyWithKey(property)
                .map(Property::getValue)
                .orElseThrow(() -> new IllegalArgumentException("Can't resolve property " + component + "@" + property));
    }
}