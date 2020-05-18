package io.microconfig.osdf.chaos;

import io.microconfig.osdf.istio.faults.Fault;
import lombok.AllArgsConstructor;
import lombok.Getter;

import static io.microconfig.osdf.istio.faults.Fault.fault;

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

    public Fault getHttpFault(Integer severity) {
        return fault(httpErrorCode, severity, httpDelay, severity);
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
