package io.microconfig.osdf.istio.faults;

import lombok.AllArgsConstructor;

import java.util.Map;

import static io.microconfig.osdf.utils.YamlUtils.getInt;
import static io.microconfig.osdf.utils.YamlUtils.getString;
import static java.util.Map.of;


@AllArgsConstructor
public class Fault {
    private final Integer httpErrorCode;
    private final Integer httpErrorPercentage;
    private final Integer httpDelayInSec;
    private final Integer httpDelayPercentage;

    public Object toYaml() {
        if (httpErrorCode != null && httpDelayInSec != null) {
            return of(
                    "abort", of(
                            "httpStatus", httpErrorCode,
                            "percentage", of(
                                    "value", httpErrorPercentage
                            )
                    ),
                    "delay", of(
                            "fixedDelay", httpDelayInSec + "s",
                            "percentage", of(
                                    "value", httpDelayPercentage
                            )
                    )
            );
        }
        if (httpErrorCode != null) {
            return of(
                    "abort", of(
                            "httpStatus", httpErrorCode,
                            "percentage", of(
                                    "value", httpErrorPercentage
                            )
                    )
            );
        }
        if (httpDelayInSec != null) {
            return of(
                    "delay", of(
                            "fixedDelay", httpDelayInSec + "s",
                            "percentage", of(
                                    "value", httpDelayPercentage
                            )
                    )
            );
        }
        throw new RuntimeException("Unknown type of Istio fault");
    }

    @SuppressWarnings("unchecked")
    static public Fault fromYaml(Object faultObject) {
        Map<String, Object> fault = (Map<String, Object>) faultObject;
        if (fault.containsKey("abort") && fault.containsKey("delay")) {
            Integer httpStatus = getInt(fault, "abort", "httpStatus");
            Integer httpErrorPercentage = getInt(fault, "abort", "percentage", "value");
            Integer httpDelayInSec = Integer.parseInt(getString(fault, "delay", "fixedDelay").split("s")[0]);
            Integer httpDelayPercentage = getInt(fault, "delay", "percentage", "value");
            return new Fault(httpStatus, httpErrorPercentage, httpDelayInSec, httpDelayPercentage);
        }

        if (fault.containsKey("abort")) {
            Integer httpStatus = getInt(fault, "abort", "httpStatus");
            Integer percentageValue = getInt(fault, "abort", "percentage", "value");
            return new Fault(httpStatus, percentageValue, null, null);
        }

        if (fault.containsKey("delay")) {
            Integer delay = Integer.parseInt(getString(fault, "delay", "fixedDelay").split("s")[0]);
            Integer percentageValue = getInt(fault, "delay", "percentage", "value");
            return new Fault(null, null, delay, percentageValue);
        }

        throw new RuntimeException("Unknown fault injection in virtual service");
    }

    static public Fault fault(Integer httpErrorCode, Integer httpErrorPercentage, Integer httpDelayInSec, Integer httpDelayPercentage) {
        return new Fault(httpErrorCode, httpErrorPercentage, httpDelayInSec, httpDelayPercentage);
    }

}