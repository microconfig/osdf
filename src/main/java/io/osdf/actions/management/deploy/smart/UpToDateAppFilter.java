package io.osdf.actions.management.deploy.smart;

import io.osdf.common.exceptions.OSDFException;
import io.osdf.core.application.core.Application;
import io.osdf.core.application.job.JobApplication;
import io.osdf.core.application.service.ServiceApplication;
import io.osdf.core.connection.cli.ClusterCli;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static io.osdf.actions.management.deploy.smart.checker.UpToDateJobChecker.upToDateJobChecker;
import static io.osdf.actions.management.deploy.smart.checker.UpToDateServiceChecker.upToDateDeploymentChecker;
import static io.osdf.common.utils.ThreadUtils.runInParallel;
import static java.util.function.Predicate.not;
import static java.util.stream.Collectors.toUnmodifiableList;

@RequiredArgsConstructor
public class UpToDateAppFilter {
    private final ClusterCli cli;

    public static UpToDateAppFilter upToDateAppFilter(ClusterCli cli) {
        return new UpToDateAppFilter(cli);
    }

    public List<Application> filter(List<? extends Application> apps) {
        return runInParallel(apps.size(), () ->
                apps.parallelStream()
                        .filter(not(this::isUpToDate))
                        .collect(toUnmodifiableList())
        );
    }

    private boolean isUpToDate(Application app) {
        if (app instanceof ServiceApplication) {
            return upToDateDeploymentChecker(cli).check(app);
        }
        if (app instanceof JobApplication) {
            return upToDateJobChecker(cli).check(app);
        }
        throw new OSDFException("Unknown app type: " + app.getClass().getSimpleName());
    }
}
