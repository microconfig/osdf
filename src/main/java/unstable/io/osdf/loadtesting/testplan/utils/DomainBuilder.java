package unstable.io.osdf.loadtesting.testplan.utils;

import io.osdf.common.exceptions.OSDFException;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@RequiredArgsConstructor
public class DomainBuilder {
    private final Map<String, String> componentsRoutes;

    public static DomainBuilder domainBuilder(Map<String, String> componentsRoutes) {
        return new DomainBuilder(componentsRoutes);
    }

    public String prepareDomain(Map<String, Object> requestConfig) {
        if (requestConfig.containsKey("component"))
            return componentsRoutes.get(String.valueOf(requestConfig.get("component")));
        if (requestConfig.containsKey("domain"))
            return String.valueOf(requestConfig.get("domain"));
        throw new OSDFException("The domain request or target component name is null");
    }
}
