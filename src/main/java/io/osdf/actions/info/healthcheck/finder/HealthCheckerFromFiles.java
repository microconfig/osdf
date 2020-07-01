package io.osdf.actions.info.healthcheck.finder;

import io.osdf.actions.info.healthcheck.healthchecker.HealthChecker;
import io.osdf.core.service.core.deployment.ServiceDeployment;
import io.osdf.core.service.local.ServiceFiles;
import io.osdf.core.cluster.pod.Pod;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static io.osdf.actions.info.healthcheck.finder.HealthCheckerFinder.healthCheckerFinder;
import static java.util.stream.Collectors.toUnmodifiableList;

@Getter
@RequiredArgsConstructor
public class HealthCheckerFromFiles {
    private final List<Pod> pods;
    private final List<Boolean> podsHealth;
    private final boolean healthy;

    public static HealthCheckerFromFiles podsInfo(ServiceDeployment deployment, ServiceFiles files) {
        return podsInfo(deployment, files, 0);
    }

    public static HealthCheckerFromFiles podsInfo(ServiceDeployment deployment, ServiceFiles files, int timeout) {
        HealthChecker healthChecker = healthCheckerFinder(files, timeout).get();

        List<Pod> pods = deployment.pods();
        List<Boolean> podsHealth = pods.parallelStream()
                .map(healthChecker::check)
                .collect(toUnmodifiableList());
        boolean healthy = podsHealth.stream().allMatch(t -> t);
        return new HealthCheckerFromFiles(pods, podsHealth, healthy);
    }
}
