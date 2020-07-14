package io.osdf.actions.info.healthcheck.app;

import io.osdf.common.exceptions.OSDFException;
import io.osdf.core.application.core.Application;
import io.osdf.core.application.job.JobApplication;
import io.osdf.core.application.service.ServiceApplication;
import io.osdf.core.connection.cli.ClusterCli;

import static io.osdf.actions.info.healthcheck.app.JobHealthChecker.jobHealthChecker;
import static io.osdf.actions.info.healthcheck.app.ServiceHealthChecker.serviceHealthChecker;

public interface AppHealthChecker {
    static AppHealthChecker of(Application app, ClusterCli cli) {
        if (app instanceof ServiceApplication) {
            return serviceHealthChecker(cli);
        }
        if (app instanceof JobApplication) {
            return jobHealthChecker(cli);
        }
        throw new OSDFException("Unknown app type: " + app.getClass().getSimpleName());
    }

    boolean check(Application application);
}
