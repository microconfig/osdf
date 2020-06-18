package io.microconfig.osdf.healthcheck;

import io.microconfig.osdf.service.deployment.checkers.healthcheck.HealthChecker;
import io.microconfig.osdf.service.deployment.ServiceDeployment;
import io.microconfig.osdf.service.files.ServiceFiles;
import io.cluster.old.cluster.pod.Pod;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static io.microconfig.osdf.service.deployment.checkers.healthcheck.HealthCheckerFinder.healthCheckerFinder;
import static java.util.stream.Collectors.toUnmodifiableList;

@Getter
@RequiredArgsConstructor
public class HealthcheckerFromFiles {
    private final List<Pod> pods;
    private final List<Boolean> podsHealth;
    private final boolean healthy;

    public static HealthcheckerFromFiles podsInfo(ServiceDeployment deployment, ServiceFiles files) {
        return podsInfo(deployment, files, 0);
    }

    public static HealthcheckerFromFiles podsInfo(ServiceDeployment deployment, ServiceFiles files, int timeout) {
        HealthChecker healthChecker = healthCheckerFinder(files, timeout).get();

        List<Pod> pods = deployment.pods();
        List<Boolean> podsHealth = pods.parallelStream()
                .map(healthChecker::check)
                .collect(toUnmodifiableList());
        boolean healthy = podsHealth.stream().allMatch(t -> t);
        return new HealthcheckerFromFiles(pods, podsHealth, healthy);
    }
}
