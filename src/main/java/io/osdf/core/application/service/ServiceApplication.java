package io.osdf.core.application.service;

import io.osdf.core.application.ApplicationManager;
import io.osdf.core.application.CoreDescription;
import io.osdf.core.application.ResourceDescription;
import io.osdf.core.application.local.ApplicationFiles;
import io.osdf.core.cluster.deployment.ClusterDeployment;
import io.osdf.core.connection.cli.ClusterCli;
import lombok.RequiredArgsConstructor;

import static io.osdf.common.utils.YamlUtils.createFromString;
import static io.osdf.core.application.ApplicationManager.applicationManager;
import static io.osdf.core.application.DescriptionUploader.descriptionUploader;
import static io.osdf.core.cluster.deployment.ClusterDeploymentImpl.clusterDeployment;
import static java.util.Map.of;

@RequiredArgsConstructor
public class ServiceApplication {
    private final String name;
    private final ApplicationFiles files;
    private final ApplicationManager manager;
    private final ClusterCli cli;

    private CoreDescription coreDescription = null;
    private ClusterDeployment deployment = null;

    public static ServiceApplication serviceApplication(ApplicationFiles files, ClusterCli cli) {
        return new ServiceApplication(files.name(), files, applicationManager(files.name(), cli), cli);
    }

    public void uploadDescription() {
        descriptionUploader(cli).upload(name + "-osdf", of(
                "service", ServiceDescription.from(files),
                "core", CoreDescription.from(files)
        ));
    }

    public ClusterDeployment deployment() {
        if (deployment != null) return deployment;
        ResourceDescription description = createFromString(ServiceDescription.class, manager.applicationConfig("service"))
                .getDeployment();
        deployment = clusterDeployment(description.getName(), description.getKind(), cli);
        return deployment;
    }

    public CoreDescription coreDescription() {
        if (coreDescription != null) return coreDescription;
        coreDescription = createFromString(CoreDescription.class, manager.applicationConfig("core"));
        return coreDescription;
    }

    public String name() {
        return name;
    }

    public ApplicationFiles files() {
        return files;
    }

    public boolean exists() {
        return manager.applicationExists();
    }

}
