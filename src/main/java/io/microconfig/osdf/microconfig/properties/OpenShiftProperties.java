package io.microconfig.osdf.microconfig.properties;

import lombok.RequiredArgsConstructor;

import static io.microconfig.core.configtypes.StandardConfigType.DEPLOY;


@RequiredArgsConstructor
public class OpenShiftProperties {
    private final PropertyGetter propertyGetter;

    public static OpenShiftProperties properties(PropertyGetter propertyGetter) {
        return new OpenShiftProperties(propertyGetter);
    }

    public String clusterUrl() {
        return propertyGetter.get(DEPLOY, "openshift-urls", "cluster.url.api");
    }

    public String project() {
        return propertyGetter.get(DEPLOY, "openshift-urls", "project");
    }
}
