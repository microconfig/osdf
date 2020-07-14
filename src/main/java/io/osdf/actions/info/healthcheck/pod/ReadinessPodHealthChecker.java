package io.osdf.actions.info.healthcheck.pod;

import io.osdf.common.exceptions.OSDFException;
import io.osdf.core.cluster.pod.Pod;
import lombok.RequiredArgsConstructor;

import static io.osdf.common.utils.ThreadUtils.calcSecFrom;
import static io.osdf.common.utils.ThreadUtils.sleepSec;
import static java.lang.System.currentTimeMillis;

@RequiredArgsConstructor
public class ReadinessPodHealthChecker implements PodHealthChecker {
    private final int timeoutInSec;

    public static ReadinessPodHealthChecker readinessHealthChecker(int timeout) {
        return new ReadinessPodHealthChecker(timeout);
    }

    @Override
    public boolean check(Pod pod) {
        long startTime = currentTimeMillis();
        while (true) {
            try {
                if (pod.isReady()) return true;
            } catch (OSDFException e) {
                return false;
            }
            if (calcSecFrom(startTime) > timeoutInSec) return false;
            sleepSec(1);
        }
    }
}
