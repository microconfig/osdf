package io.microconfig.osdf.microconfig.properties;

import lombok.RequiredArgsConstructor;

import static io.microconfig.factory.configtypes.StandardConfigTypes.DEPLOY;
import static io.microconfig.factory.configtypes.StandardConfigTypes.PROCESS;
import static io.microconfig.osdf.utils.StringUtils.castToInteger;

@RequiredArgsConstructor
public class HealthCheckProperties {
    private final PropertyGetter propertyGetter;

    public static HealthCheckProperties properties(PropertyGetter propertyGetter) {
        return new HealthCheckProperties(propertyGetter);
    }

    public String marker(String componentName) {
        return propertyGetter.get(PROCESS, componentName, "healthcheck.marker.success");
    }

    public int timeoutInSec(String componentName) {
        String waitSecProperty = propertyGetter.get(DEPLOY, componentName, "osdf.start.waitSec");
        Integer waitSec = castToInteger(waitSecProperty);
        return waitSec != null ? waitSec : 25;
    }
}
