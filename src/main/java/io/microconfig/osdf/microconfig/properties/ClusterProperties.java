package io.microconfig.osdf.microconfig.properties;

import lombok.RequiredArgsConstructor;

import static io.microconfig.core.configtypes.StandardConfigType.DEPLOY;


@RequiredArgsConstructor
public class ClusterProperties {
    private final String COMPONENT_NAME = "k8s-cluster";

    private final PropertyGetter propertyGetter;

    public static ClusterProperties properties(PropertyGetter propertyGetter) {
        return new ClusterProperties(propertyGetter);
    }

    public String clusterUrl() {
        return propertyGetter.get(DEPLOY, COMPONENT_NAME, "cluster.url.api");
    }

    public String project() {
        return propertyGetter.get(DEPLOY, COMPONENT_NAME, "project");
    }
}