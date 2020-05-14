package io.microconfig.osdf.istio.faults;

import java.util.Map;
import java.util.Random;

import static io.microconfig.osdf.istio.faults.AbortFault.abortFault;
import static io.microconfig.osdf.istio.faults.DelayFault.delayFault;
import static io.microconfig.osdf.utils.YamlUtils.getInt;
import static io.microconfig.osdf.utils.YamlUtils.getString;

@SuppressWarnings("unchecked")
public interface Fault {
    Object toYaml();

    static Fault fromYaml(Object faultObject) {
        Map<String, Object> fault = (Map<String, Object>) faultObject;
        if (fault.containsKey("abort")) {
            String httpStatus = getString(fault, "abort", "httpStatus");
            Integer percentageValue = getInt(fault, "abort", "percentage", "value");
            return new AbortFault(httpStatus, percentageValue);
        } else if (fault.containsKey("delay")) {
            String delay = getString(fault, "delay", "fixedDelay");
            Integer percentageValue = getInt(fault, "delay", "percentage", "value");
            return new DelayFault(delay, percentageValue);
        } else throw new RuntimeException("Unknown fault injection in virtual service");
    }

    static Fault faultFromArgs(String faultType, Integer chaosSeverity) {

        Random r = new Random();

        if (faultType.equals("delay")) {
            return delayFault((5 + r.nextInt(10)) + "s", chaosSeverity);
        }
        if (faultType.equals("abort")) {
            return abortFault("555", chaosSeverity);
        }
        throw new RuntimeException("Unknown type of fault: " + faultType);
    }
}
