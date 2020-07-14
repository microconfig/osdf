package io.osdf.core.application.service;

import io.osdf.common.exceptions.OSDFException;
import io.osdf.core.application.core.AbstractApplication;
import io.osdf.core.application.core.Application;
import io.osdf.core.application.core.description.CoreDescription;
import io.osdf.core.application.core.description.ResourceDescription;
import io.osdf.core.application.core.files.ApplicationFiles;
import io.osdf.core.cluster.deployment.ClusterDeployment;
import io.osdf.core.connection.cli.ClusterCli;
import lombok.RequiredArgsConstructor;

import static io.osdf.core.application.core.AbstractApplication.application;
import static io.osdf.core.application.core.description.DescriptionUploader.descriptionUploader;
import static io.osdf.core.cluster.deployment.ClusterDeploymentImpl.clusterDeployment;
import static java.util.Map.of;

@RequiredArgsConstructor
public class ServiceApplication implements Application {
    private final ClusterCli cli;
    private final AbstractApplication app;

    private ClusterDeployment deployment = null;

    public static ServiceApplication serviceApplication(ApplicationFiles files, ClusterCli cli) {
        return new ServiceApplication(cli, application(cli, files));
    }

    public static ServiceApplication serviceApplication(Application app) {
        if (!(app instanceof ServiceApplication)) throw new OSDFException(app.name() + " is not a service");
        return (ServiceApplication) app;
    }

    public void uploadDescription() {
        descriptionUploader(cli).upload(app.descriptionConfigMapName(), of(
                "service", ServiceDescription.from(app.files()),
                "core", CoreDescription.from(app.files())
        ));
    }

    public ClusterDeployment deployment() {
        if (deployment != null) return deployment;
        ResourceDescription description = app.loadDescription(ServiceDescription.class, "service").getDeployment();
        return deployment = clusterDeployment(description.getName(), description.getKind(), cli);
    }

    @Override
    public String name() {
        return app.name();
    }

    @Override
    public boolean exists() {
        return app.exists();
    }

    @Override
    public void delete() {
        app.delete();
    }

    @Override
    public ApplicationFiles files() {
        return app.files();
    }

    @Override
    public CoreDescription coreDescription() {
        return app.coreDescription();
    }

}
