package io.microconfig.osdf.istio;

import io.microconfig.osdf.exceptions.OSDFException;
import lombok.RequiredArgsConstructor;

import java.util.Map;

import static io.microconfig.osdf.utils.YamlUtils.getInt;
import static java.util.Map.of;

@RequiredArgsConstructor
public class AbortFault {
    private static final String ABORT = "abort";
    private static final String HTTP_STATUS = "httpStatus";
    private static final String VALUE = "value";
    private static final String PERCENTAGE = "percentage";

    private final Integer httpErrorCode;
    private final Integer httpErrorPercentage;

    @SuppressWarnings("unchecked")
    public static AbortFault fromYaml(Object o) {
        Map<String, Object> yaml = (Map<String, Object>) o;
        Integer httpStatus = getInt(yaml, ABORT, HTTP_STATUS);
        Integer httpErrorPercentage = getInt(yaml, ABORT, PERCENTAGE, VALUE);
        return new AbortFault(httpStatus, httpErrorPercentage);
    }

    public static AbortFault fromValues(Integer httpErrorCode, Integer httpErrorPercentage) {
        if (httpErrorCode == null && httpErrorPercentage == null) return null;
        return new AbortFault(httpErrorCode, httpErrorPercentage);
    }

    public Object toYaml() {
        return of(
                HTTP_STATUS, httpErrorCode,
                PERCENTAGE, of(
                        VALUE, httpErrorPercentage
                )

        );
    }

    public void checkCorrectness() {
        if (httpErrorCode == null || httpErrorPercentage == null)
            throw new OSDFException("Incorrect abort fault");
        if (httpErrorPercentage < 0 || httpErrorPercentage > 100)
            throw new OSDFException("Incorrect percentage value");
        if (httpErrorCode < 500 || httpErrorCode > 599)
            throw new OSDFException("Incorrect error code value");
    }
}
