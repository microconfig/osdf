package io.osdf.core.application.plain;

import io.osdf.common.exceptions.OSDFException;
import io.osdf.core.application.core.AbstractApplication;
import io.osdf.core.application.core.Application;
import io.osdf.core.application.core.description.CoreDescription;
import io.osdf.core.application.core.files.ApplicationFiles;
import io.osdf.core.connection.cli.ClusterCli;

import static io.osdf.core.cluster.configmap.ConfigMapLoader.configMapLoader;
import static java.util.Map.of;

public class PlainApplication extends AbstractApplication {
    private final ClusterCli cli;

    public PlainApplication(ApplicationFiles files, ClusterCli cli) {
        super(files.name(), cli, files);
        this.cli = cli;
    }

    public static PlainApplication plainApplication(ApplicationFiles files, ClusterCli cli) {
        return new PlainApplication(files, cli);
    }

    public static PlainApplication plainApplication(Application app) {
        if (!(app instanceof PlainApplication)) throw new OSDFException(app.name() + " is not a plain app");
        return (PlainApplication) app;
    }

    public void uploadDescription() {
        configMapLoader(cli).upload(super.descriptionConfigMapName(), of(
                "core", CoreDescription.from(super.files()),
                "plain", PlainAppDescription.from(super.files())
        ));
    }
}
