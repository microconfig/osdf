package io.osdf.actions.management.deploy.deployer;

import io.osdf.core.application.core.Application;
import io.osdf.core.application.job.JobApplication;
import io.osdf.core.application.plain.PlainApplication;
import io.osdf.core.application.service.ServiceApplication;
import io.osdf.core.connection.cli.ClusterCli;

import java.util.Map;

import static io.osdf.actions.management.deploy.deployer.job.DeployableJob.deployableJob;
import static io.osdf.actions.management.deploy.deployer.plain.DeployablePlainApp.deployablePlainApp;
import static io.osdf.actions.management.deploy.deployer.service.DeployableService.deployableService;
import static io.osdf.common.utils.MappingUtils.fromMapping;

public interface Deployable {
    static Deployable of(Application app, ClusterCli cli) {
        return fromMapping(app, Map.of(
                ServiceApplication.class, () -> deployableService((ServiceApplication) app, cli),
                JobApplication.class, () -> deployableJob((JobApplication) app, cli),
                PlainApplication.class, () -> deployablePlainApp((PlainApplication) app, cli)
        ));
    }

    String name();

    boolean deploy();

    AppHealth check();
}
