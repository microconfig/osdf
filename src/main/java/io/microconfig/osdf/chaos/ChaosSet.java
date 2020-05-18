package io.microconfig.osdf.chaos;

import io.microconfig.osdf.istio.faults.Fault;
import lombok.AllArgsConstructor;
import lombok.Getter;

import static io.microconfig.osdf.istio.faults.Fault.abortFault;
import static io.microconfig.osdf.istio.faults.Fault.delayFault;

@Getter
@AllArgsConstructor
public class ChaosSet {
    private final Integer httpErrorCode;
    private final Integer httpDelay;
    private final Integer ioStressTimeout;
    private final Integer killPodTimeout;

    public static ChaosSet chaosSet(Integer httpErrorCode, Integer httpDelay, Integer ioStressTimeout, Integer killPodTimeout) {
        return new ChaosSet(httpErrorCode, httpDelay, ioStressTimeout, killPodTimeout);
    }

    public Fault getHttpDelayFault(Integer severity) {
        return delayFault(httpDelay, severity);
    }

    public Fault getHttpErrorFault(Integer severity) {
        return abortFault(httpErrorCode, severity);
    }

    public boolean isHttpError() {
        return httpErrorCode != null;
    }

    public boolean isHttpDelay() {
        return httpDelay != null;
    }

    public boolean isIOStress() {
        return ioStressTimeout != null;
    }

    public boolean isKillPod() {
        return killPodTimeout != null;
    }

}
