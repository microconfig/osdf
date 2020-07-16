package io.osdf.actions.info.healthcheck.app;

import io.osdf.core.application.core.Application;
import io.osdf.core.application.job.JobApplication;
import io.osdf.core.connection.cli.ClusterCli;
import lombok.RequiredArgsConstructor;

import java.nio.file.Path;

import static io.osdf.common.yaml.YamlObject.yaml;
import static io.osdf.core.application.job.JobApplication.jobApp;
import static java.util.Objects.requireNonNullElse;

@RequiredArgsConstructor
public class JobHealthChecker implements AppHealthChecker {
    private final ClusterCli cli;

    public static JobHealthChecker jobHealthChecker(ClusterCli cli) {
        return new JobHealthChecker(cli);
    }

    @Override
    public boolean check(Application app) {
        JobApplication jobApp = jobApp(app);
        return cli.execute("wait --for=condition=complete --timeout=" + getWaitTimeout(jobApp) + "s job/" + jobApp.job().name())
                .ok();
    }

    private int getWaitTimeout(JobApplication jobApp) {
        Integer timeout = yaml(Path.of(jobApp.files().metadata().getMainResource().getPath()))
                .get("spec.activeDeadlineSeconds");
        return requireNonNullElse(timeout, 60);
    }
}
