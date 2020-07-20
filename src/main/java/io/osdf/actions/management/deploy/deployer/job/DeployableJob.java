package io.osdf.actions.management.deploy.deployer.job;

import io.osdf.actions.management.deploy.deployer.Deployable;
import io.osdf.core.application.job.JobApplication;
import io.osdf.core.connection.cli.ClusterCli;
import lombok.RequiredArgsConstructor;

import static io.osdf.actions.info.healthcheck.app.JobHealthChecker.jobHealthChecker;
import static io.osdf.actions.management.deploy.deployer.job.JobRunner.jobRunner;

@RequiredArgsConstructor
public class DeployableJob implements Deployable {
    private final JobApplication jobApp;
    private final ClusterCli cli;

    public static DeployableJob deployableJob(JobApplication jobApp, ClusterCli cli) {
        return new DeployableJob(jobApp, cli);
    }

    @Override
    public String name() {
        return jobApp.name();
    }

    @Override
    public boolean deploy() {
        return jobRunner(cli).runJob(jobApp);
    }

    @Override
    public boolean check() {
        return jobHealthChecker(cli).check(jobApp);
    }
}
