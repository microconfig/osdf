package io.osdf.actions.management.deploy.deployer;

import io.osdf.common.exceptions.OSDFException;
import io.osdf.core.application.core.Application;
import io.osdf.core.application.job.JobApplication;
import io.osdf.core.application.service.ServiceApplication;
import io.osdf.core.connection.cli.ClusterCli;

import static io.osdf.actions.management.deploy.deployer.job.DeployableJob.deployableJob;
import static io.osdf.actions.management.deploy.deployer.service.DeployableService.deployableService;

public interface Deployable {
    static Deployable of(Application app, ClusterCli cli) {
        if (app instanceof ServiceApplication) {
            return deployableService((ServiceApplication) app, cli);
        }
        if (app instanceof JobApplication) {
            return deployableJob((JobApplication) app, cli);
        }
        throw new OSDFException("Unknown application type: " + app.getClass().getSimpleName());
    }

    String name();

    void deploy();

    boolean check();
}
