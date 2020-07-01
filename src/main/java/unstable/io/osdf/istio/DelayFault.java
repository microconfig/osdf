package unstable.io.osdf.istio;

import io.osdf.common.exceptions.OSDFException;
import lombok.RequiredArgsConstructor;

import java.util.Map;

import static io.osdf.common.utils.YamlUtils.getInt;
import static io.osdf.common.utils.YamlUtils.getString;
import static java.lang.Integer.parseInt;
import static java.util.Map.of;

@RequiredArgsConstructor
public class DelayFault {
    private static final String DELAY = "delay";
    private static final String VALUE = "value";
    private static final String PERCENTAGE = "percentage";
    private static final String FIXED_DELAY = "fixedDelay";

    private final Integer httpDelayInSec;
    private final Integer httpDelayPercentage;

    @SuppressWarnings("unchecked")
    public static DelayFault fromYaml(Object o) {
        Map<String, Object> yaml = (Map<String, Object>) o;
        Integer httpDelayInSec = parseInt(getString(yaml, DELAY, FIXED_DELAY).split("s")[0]);
        Integer httpDelayPercentage = getInt(yaml, DELAY, PERCENTAGE, VALUE);
        return new DelayFault(httpDelayInSec, httpDelayPercentage);
    }

    public static DelayFault fromValues(Integer httpDelayInSec, Integer httpDelayPercentage) {
        if (httpDelayInSec == null && httpDelayPercentage == null) return null;
        return new DelayFault(httpDelayInSec, httpDelayPercentage);
    }

    public Object toYaml() {
        return of(
                FIXED_DELAY, httpDelayInSec + "s",
                PERCENTAGE, of(
                        VALUE, httpDelayPercentage
                )
        );
    }

    public void checkCorrectness() {
        if (httpDelayInSec == null || httpDelayPercentage == null)
            throw new OSDFException("Incorrect delay fault");
        if (httpDelayPercentage < 0 || httpDelayPercentage > 100)
            throw new OSDFException("Incorrect percentage value");
        if (httpDelayInSec < 1)
            throw new OSDFException("Incorrect delay value");
    }
}
