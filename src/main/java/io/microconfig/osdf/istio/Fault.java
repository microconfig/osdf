package io.microconfig.osdf.istio;

import io.microconfig.osdf.exceptions.OSDFException;
import lombok.AllArgsConstructor;

import java.util.Map;

import static io.microconfig.osdf.utils.YamlUtils.getInt;
import static io.microconfig.osdf.utils.YamlUtils.getString;
import static java.util.Map.of;


@AllArgsConstructor
public class Fault {
    private static final String ABORT = "abort";
    private static final String DELAY = "delay";
    private static final String HTTP_STATUS = "httpStatus";
    private static final String VALUE = "value";
    private static final String PERCENTAGE = "percentage";
    private static final String FIXED_DELAY = "fixedDelay";


    private final Integer httpErrorCode;
    private final Integer httpErrorPercentage;
    private final Integer httpDelayInSec;
    private final Integer httpDelayPercentage;

    @SuppressWarnings("unchecked")
    public static Fault fromYaml(Object faultObject) {
        Map<String, Object> fault = (Map<String, Object>) faultObject;
        if (fault.containsKey(ABORT) && fault.containsKey(DELAY)) {
            Integer httpStatus = getInt(fault, ABORT, HTTP_STATUS);
            Integer httpErrorPercentage = getInt(fault, ABORT, PERCENTAGE, VALUE);
            Integer httpDelayInSec = Integer.parseInt(getString(fault, DELAY, FIXED_DELAY).split("s")[0]);
            Integer httpDelayPercentage = getInt(fault, DELAY, PERCENTAGE, VALUE);
            return new Fault(httpStatus, httpErrorPercentage, httpDelayInSec, httpDelayPercentage);
        }

        if (fault.containsKey(ABORT)) {
            Integer httpStatus = getInt(fault, ABORT, HTTP_STATUS);
            Integer percentageValue = getInt(fault, ABORT, PERCENTAGE, VALUE);
            return new Fault(httpStatus, percentageValue, null, null);
        }

        if (fault.containsKey(DELAY)) {
            Integer delay = Integer.parseInt(getString(fault, DELAY, FIXED_DELAY).split("s")[0]);
            Integer percentageValue = getInt(fault, DELAY, PERCENTAGE, VALUE);
            return new Fault(null, null, delay, percentageValue);
        }

        throw new OSDFException("Unknown fault injection in virtual service");
    }

    public static Fault fault(Integer httpErrorCode, Integer httpErrorPercentage, Integer httpDelayInSec, Integer httpDelayPercentage) {
        return new Fault(httpErrorCode, httpErrorPercentage, httpDelayInSec, httpDelayPercentage);
    }

    public Object toYaml() {
        if (httpErrorCode != null && httpDelayInSec != null) {
            return of(
                    ABORT, of(
                            HTTP_STATUS, httpErrorCode,
                            PERCENTAGE, of(
                                    VALUE, httpErrorPercentage
                            )
                    ),
                    DELAY, of(
                            FIXED_DELAY, httpDelayInSec + "s",
                            PERCENTAGE, of(
                                    VALUE, httpDelayPercentage
                            )
                    )
            );
        }
        if (httpErrorCode != null) {
            return of(
                    ABORT, of(
                            HTTP_STATUS, httpErrorCode,
                            PERCENTAGE, of(
                                    VALUE, httpErrorPercentage
                            )
                    )
            );
        }
        if (httpDelayInSec != null) {
            return of(
                    DELAY, of(
                            FIXED_DELAY, httpDelayInSec + "s",
                            PERCENTAGE, of(
                                    VALUE, httpDelayPercentage
                            )
                    )
            );
        }
        throw new OSDFException("Unknown type of Istio fault");
    }

    public void checkCorrectness() {
        if (!isAbort() && !isDelay()) throw new OSDFException("Fault incorrect");

        if (isAbort()) {
            if (httpErrorPercentage < 0 || httpErrorPercentage > 100)
                throw new OSDFException("Incorrect percentage value");
            if (httpErrorCode < 500 || httpErrorCode > 599)
                throw new OSDFException("Incorrect error code value");
        }

        if (isDelay()) {
            if (httpDelayPercentage < 0 || httpDelayPercentage > 100)
                throw new OSDFException("Incorrect percentage value");
            if (httpDelayInSec < 1)
                throw new OSDFException("Incorrect delay value");
        }
    }

    private boolean isAbort() {
        return httpErrorCode != null && httpErrorPercentage != null;
    }

    private boolean isDelay() {
        return httpDelayInSec != null && httpDelayPercentage != null;
    }
}
