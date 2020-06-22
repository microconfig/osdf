package io.microconfig.osdf.microconfig.properties;

import lombok.RequiredArgsConstructor;

import static io.microconfig.core.configtypes.StandardConfigType.APPLICATION;

@RequiredArgsConstructor
public class OSDFDownloadProperties {
    private final static String COMPONENT_NAME = "osdf-artifact";

    private final PropertyGetter propertyGetter;

    public static OSDFDownloadProperties properties(PropertyGetter propertyGetter) {
        return new OSDFDownloadProperties(propertyGetter);
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
