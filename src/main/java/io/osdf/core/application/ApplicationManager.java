package io.osdf.core.application;

import io.osdf.core.cluster.resource.ClusterResourceImpl;
import io.osdf.core.connection.cli.ClusterCli;
import lombok.RequiredArgsConstructor;

import static io.osdf.common.utils.YamlUtils.createFromString;
import static io.osdf.core.cluster.resource.ClusterResourceImpl.clusterResource;

@RequiredArgsConstructor
public class ApplicationManager {
    private final String name;
    private final ClusterCli cli;

    public static ApplicationManager applicationManager(String name, ClusterCli cli) {
        return new ApplicationManager(name, cli);
    }

    public boolean applicationExists() {
        return descriptionExists();
    }

    public String applicationConfig(String key) {
        return cli.execute("get configmap " + name + "-osdf -o custom-columns=\"config:.data." + key + "\"")
                .throwExceptionIfError()
                .getOutput()
                .replaceFirst("config\n", "");
    }

    public void delete() {
        createFromString(CoreDescription.class, applicationConfig("core"))
                .getResources().stream()
                .map(ClusterResourceImpl::fromOpenShiftNotation)
                .forEach(resource -> resource.delete(cli));
        clusterResource("configmap", name + "-osdf").delete(cli);
    }

    private boolean descriptionExists() {
        return cli.execute("get configmap " + name + "-osdf").ok();
    }
}
