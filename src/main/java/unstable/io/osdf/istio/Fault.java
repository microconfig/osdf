package unstable.io.osdf.istio;

import lombok.AllArgsConstructor;

import java.util.HashMap;
import java.util.Map;


@AllArgsConstructor
public class Fault {
    private static final String ABORT = "abort";
    private static final String DELAY = "delay";

    private final AbortFault abortFault;
    private final DelayFault delayFault;

    public static Fault fromYaml(Object faultObject) {
        AbortFault abortFault = AbortFault.fromYaml(faultObject);
        DelayFault delayFault = DelayFault.fromYaml(faultObject);
        return new Fault(abortFault, delayFault);
    }

    public static Fault fault(Integer httpErrorCode, Integer httpErrorPercentage, Integer httpDelayInSec, Integer httpDelayPercentage) {
        return new Fault(
                AbortFault.fromValues(httpErrorCode, httpErrorPercentage),
                DelayFault.fromValues(httpDelayInSec, httpDelayPercentage)
        );
    }

    public Object toYaml() {
        Map<String, Object> fault = new HashMap<>();
        if (abortFault != null) {
            fault.put(ABORT, abortFault.toYaml());
        }
        if (delayFault != null) {
            fault.put(DELAY, delayFault.toYaml());
        }
        return fault;
    }

    public void checkCorrectness() {
        if (delayFault != null) {
            delayFault.checkCorrectness();
        }
        if (abortFault != null) {
            abortFault.checkCorrectness();
        }
    }
}
