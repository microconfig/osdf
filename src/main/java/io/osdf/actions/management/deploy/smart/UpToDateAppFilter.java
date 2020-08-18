package io.osdf.actions.management.deploy.smart;

import io.osdf.core.application.core.Application;
import io.osdf.core.application.job.JobApplication;
import io.osdf.core.application.plain.PlainApplication;
import io.osdf.core.application.service.ServiceApplication;
import io.osdf.core.connection.cli.ClusterCli;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;

import static io.osdf.actions.management.deploy.smart.checker.UpToDateJobChecker.upToDateJobChecker;
import static io.osdf.actions.management.deploy.smart.checker.UpToDatePlainAppChecker.upToDatePlainAppChecker;
import static io.osdf.actions.management.deploy.smart.checker.UpToDateServiceChecker.upToDateDeploymentChecker;
import static io.osdf.common.utils.MappingUtils.fromMapping;
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
        return fromMapping(app, Map.of(
                ServiceApplication.class, () -> upToDateDeploymentChecker(cli).check(app),
                JobApplication.class, () -> upToDateJobChecker(cli).check(app),
                PlainApplication.class, () -> upToDatePlainAppChecker().check(app)
        ));
    }
}
