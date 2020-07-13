package io.osdf.settings.version;

import io.osdf.core.local.microconfig.property.PropertyGetter;
import lombok.RequiredArgsConstructor;

import static io.microconfig.core.configtypes.StandardConfigType.APPLICATION;

@RequiredArgsConstructor
public class OsdfArtifactFromConfigs {
    private static final String COMPONENT_NAME = "osdf-artifact";

    private final PropertyGetter propertyGetter;

    public static OsdfArtifactFromConfigs osdfArtifact(PropertyGetter propertyGetter) {
        return new OsdfArtifactFromConfigs(propertyGetter);
    }

    public String version() {
        return propertyGetter.get(APPLICATION, COMPONENT_NAME, "osdf.version");
    }

    public String url() {
        return propertyGetter.get(APPLICATION, COMPONENT_NAME, "osdf.url");
    }

    public String group() {
        return propertyGetter.get(APPLICATION, COMPONENT_NAME, "osdf.group");
    }

    public String artifact() {
        return propertyGetter.get(APPLICATION, COMPONENT_NAME, "osdf.artifact");
    }
}
