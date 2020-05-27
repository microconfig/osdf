package io.microconfig.osdf.chaos;

import io.microconfig.osdf.istio.faults.Fault;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

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

    List<String> faults() {
        List<String> list = new ArrayList<>();

        if (isHttpError() || isHttpDelay()) list.add("network");
        if (isIOStress()) list.add("io");
        if (isKillPod()) list.add("pods");

        return list;
    }

}
