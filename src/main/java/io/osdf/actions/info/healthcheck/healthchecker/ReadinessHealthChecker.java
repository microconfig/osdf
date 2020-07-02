package io.osdf.actions.info.healthcheck.healthchecker;

import io.osdf.core.cluster.pod.Pod;
import lombok.RequiredArgsConstructor;

import static io.osdf.common.utils.ThreadUtils.sleepSec;
import static java.lang.System.currentTimeMillis;

@RequiredArgsConstructor
public class ReadinessHealthChecker implements HealthChecker {
    private final int timeoutInSec;

    public static ReadinessHealthChecker readinessHealthChecker(int timeout) {
        return new ReadinessHealthChecker(timeout);
    }

    @Override
    public boolean check(Pod pod) {
        long startTime = currentTimeMillis();
        while (true) {
            if (pod.isReady()) return true;
            if (calcSecFrom(startTime) > timeoutInSec) return false;
            sleepSec(1);
        }
    }

    private long calcSecFrom(long startTime) {
        return (currentTimeMillis() - startTime) / 1000;
    }
}
