package io.osdf.actions.info.healthcheck.app;

import io.osdf.core.application.core.Application;
import io.osdf.core.application.job.JobApplication;
import io.osdf.core.application.plain.PlainApplication;
import io.osdf.core.application.service.ServiceApplication;
import io.osdf.core.connection.cli.ClusterCli;

import java.util.Map;

import static io.osdf.actions.info.healthcheck.app.JobHealthChecker.jobHealthChecker;
import static io.osdf.actions.info.healthcheck.app.ServiceHealthChecker.serviceHealthChecker;
import static io.osdf.common.utils.MappingUtils.fromMapping;

public interface AppHealthChecker {
    static AppHealthChecker of(Application app, ClusterCli cli) {
        return fromMapping(app, Map.of(
                ServiceApplication.class, () -> serviceHealthChecker(cli),
                JobApplication.class, () -> jobHealthChecker(cli),
                PlainApplication.class, PlainAppHealthChecker::plainAppHealthChecker
        ));
    }

    boolean check(Application application);
}
