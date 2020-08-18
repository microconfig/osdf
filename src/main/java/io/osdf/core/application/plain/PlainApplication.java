package io.osdf.core.application.plain;

import io.osdf.core.application.core.AbstractApplication;
import io.osdf.core.application.core.Application;
import io.osdf.core.application.core.description.CoreDescription;
import io.osdf.core.application.core.files.ApplicationFiles;
import io.osdf.core.connection.cli.ClusterCli;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

import static io.osdf.core.application.core.AbstractApplication.application;
import static io.osdf.core.cluster.configmap.ConfigMapLoader.configMapLoader;
import static java.util.Map.of;

@RequiredArgsConstructor
public class PlainApplication implements Application {
    private final AbstractApplication app;
    private final ClusterCli cli;

    public static PlainApplication plainApplication(ApplicationFiles files, ClusterCli cli) {
        return new PlainApplication(application(cli, files), cli);
    }

    public void uploadDescription() {
        configMapLoader(cli).upload(app.descriptionConfigMapName(), of(
                "core", CoreDescription.from(app.files())
        ));
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
    public Optional<CoreDescription> coreDescription() {
        return app.coreDescription();
    }
}
