package io.osdf.core.connection.context;

import io.microconfig.core.properties.repository.ComponentNotFoundException;
import io.osdf.actions.init.configs.postprocess.ExternalVariablesResolver;
import io.osdf.common.exceptions.OSDFException;
import io.osdf.core.local.microconfig.property.PropertyGetter;
import io.osdf.settings.paths.OsdfPaths;
import lombok.RequiredArgsConstructor;

import static io.microconfig.core.configtypes.StandardConfigType.DEPLOY;
import static io.osdf.actions.init.configs.postprocess.ExternalVariablesResolver.externalVariablesResolver;
import static io.osdf.core.local.microconfig.property.PropertyGetter.propertyGetter;


@RequiredArgsConstructor
public class ClusterProperties {
    private static final String COMPONENT_NAME = "k8s-cluster";

    private final PropertyGetter propertyGetter;
    private final ExternalVariablesResolver externalVariablesResolver;

    public static ClusterProperties properties(OsdfPaths paths) {
        return new ClusterProperties(propertyGetter(paths), externalVariablesResolver(paths));
    }

    public String clusterUrl() {
        return property("cluster.url.api");
    }

    public String project() {
        return property("project");
    }

    private String property(String key) {
        try {
            String unresolvedProperty = propertyGetter.get(DEPLOY, COMPONENT_NAME, key);
            return externalVariablesResolver.resolveVariables(unresolvedProperty);
        } catch (ComponentNotFoundException e) {
            throw new OSDFException("Component " + COMPONENT_NAME + " not found");
        }
    }

}
