package io.microconfig.osdf.components.info;

import io.microconfig.osdf.components.checker.HealthChecker;
import io.microconfig.osdf.develop.service.deployment.ServiceDeployment;
import io.microconfig.osdf.develop.service.files.ServiceFiles;
import io.microconfig.osdf.openshift.Pod;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static io.microconfig.osdf.components.checker.HealthCheckerFinder.healthCheckerFinder;
import static java.util.stream.Collectors.toUnmodifiableList;

@Getter
@RequiredArgsConstructor
public class PodsHealthcheckInfo {
    private final List<Pod> pods;
    private final List<Boolean> podsHealth;
    private final boolean healthy;

    public static PodsHealthcheckInfo podsInfo(ServiceDeployment deployment, ServiceFiles files) {
        HealthChecker healthChecker = healthCheckerFinder(files).get();

        List<Pod> pods = deployment.pods();
        List<Boolean> podsHealth = pods.parallelStream()
                .map(healthChecker::check)
                .collect(toUnmodifiableList());
        boolean healthy = podsHealth.stream().allMatch(t -> t);
        return new PodsHealthcheckInfo(pods, podsHealth, healthy);
    }
}
