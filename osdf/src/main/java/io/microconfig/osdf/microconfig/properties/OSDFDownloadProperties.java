package io.microconfig.osdf.microconfig.properties;

import lombok.RequiredArgsConstructor;

import static io.microconfig.core.configtypes.StandardConfigType.PROCESS;

@RequiredArgsConstructor
public class OSDFDownloadProperties {
    private final PropertyGetter propertyGetter;

    public static OSDFDownloadProperties properties(PropertyGetter propertyGetter) {
        return new OSDFDownloadProperties(propertyGetter);
    }

    public String version() {
        return propertyGetter.get(PROCESS, "osdf-version", "osdf.version");
    }

    public String url() {
        return propertyGetter.get(PROCESS, "osdf-version", "osdf.url");
    }

    public String group() {
        return propertyGetter.get(PROCESS, "osdf-version", "osdf.group");
    }

    public String artifact() {
        return propertyGetter.get(PROCESS, "osdf-version", "osdf.artifact");
    }
}
