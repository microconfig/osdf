package io.osdf.settings;

import io.microconfig.core.properties.repository.ComponentNotFoundException;
import io.osdf.common.exceptions.OSDFException;
import io.osdf.core.local.microconfig.property.PropertyGetter;
import io.osdf.settings.paths.OsdfPaths;
import lombok.RequiredArgsConstructor;

import static io.microconfig.core.configtypes.StandardConfigType.APPLICATION;
import static io.osdf.common.utils.StringUtils.castToInteger;
import static io.osdf.core.local.microconfig.property.PropertyGetter.propertyGetter;

@RequiredArgsConstructor
public class OsdfConfig {
    private static final String COMPONENT_NAME = "osdf-config";

    private final PropertyGetter propertyGetter;

    public static OsdfConfig osdfConfig(OsdfPaths paths) {
        return new OsdfConfig(propertyGetter(paths));
    }

    public String version() {
        return appProperty("osdf.version");
    }

    public String url() {
        return appProperty("osdf.url");
    }

    public String group() {
        return appProperty("osdf.group");
    }

    public String artifact() {
        return appProperty("osdf.artifact");
    }

    public Integer maxParallel() {
        return castToInteger(appProperty("osdf.deploy.maxParallel"));
    }

    private String appProperty(String key) {
        try {
            return propertyGetter.get(APPLICATION, COMPONENT_NAME, key);
        } catch (ComponentNotFoundException e) {
            throw new OSDFException("Component " + COMPONENT_NAME + " not found");
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
