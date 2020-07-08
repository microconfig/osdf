package io.osdf.actions.info.healthcheck;

import io.osdf.actions.info.healthcheck.healthchecker.HealthChecker;
import io.osdf.core.application.local.ApplicationFiles;
import io.osdf.core.cluster.deployment.ClusterDeployment;
import io.osdf.core.cluster.pod.Pod;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static io.osdf.actions.info.healthcheck.finder.HealthCheckerFinder.healthCheckerFinder;
import static java.util.stream.Collectors.toUnmodifiableList;

@Getter
@RequiredArgsConstructor
public class PodsInfo {
    private final List<Pod> pods;
    private final List<Boolean> podsHealth;
    private final boolean healthy;

    public static PodsInfo podsInfo(ClusterDeployment deployment, ApplicationFiles files) {
        return podsInfo(deployment, files, 0);
    }

    public static PodsInfo podsInfo(ClusterDeployment deployment, ApplicationFiles files, int timeout) {
        HealthChecker healthChecker = healthCheckerFinder(files, timeout).find();

        List<Pod> pods = deployment.pods();
        List<Boolean> podsHealth = pods.parallelStream()
                .map(healthChecker::check)
                .collect(toUnmodifiableList());
        boolean healthy = podsHealth.stream().allMatch(t -> t);
        return new PodsInfo(pods, podsHealth, healthy);
    }
}
