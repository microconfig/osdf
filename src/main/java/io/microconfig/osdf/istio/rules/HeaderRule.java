package io.microconfig.osdf.istio.rules;

import io.microconfig.osdf.istio.Destination;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;

import static io.microconfig.osdf.utils.YamlUtils.*;

@RequiredArgsConstructor
public class HeaderRule {
    @Getter
    private final Destination destination;
    private final String headerName;
    private final String headerValue;

    public static HeaderRule headerRule(Destination destination, String headerName, String headerValue) {
        return new HeaderRule(destination, headerName, headerValue);
    }

    @SuppressWarnings("unchecked")
    public static HeaderRule fromYaml(Object ruleObject) {
        Map<String, Object> rule = (Map<String, Object>) ruleObject;

        Map<String, Object> route = (Map<String, Object>) getList(rule, "route").get(0);
        Destination destination = Destination.fromYaml(getMap(route, "destination"));

        List<Object> matches = getList(rule, "match");
        Map<String, Object> headers = getMap((Map<String, Object>) matches.get(0), "headers");
        return headers.entrySet().stream()
                .map(entry -> yamlEntryToHeaderRule(destination, entry))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Empty headers"));
    }

    @SuppressWarnings("unchecked")
    private static HeaderRule yamlEntryToHeaderRule(Destination destination, Map.Entry<String, Object> entry) {
        String value = getString((Map<String, Object>) entry.getValue(), "exact");
        return new HeaderRule(destination, entry.getKey(), value);
    }

    public Object toYaml() {
        return Map.of(
                "route", List.of(
                        Map.of(
                                "destination", destination.toYaml()
                        )
                ),
                "match", List.of(
                        Map.of(
                                "headers",
                                Map.of(
                                        headerName,
                                        Map.of(
                                                "exact",
                                                headerValue
                                        )
                                )
                        )
                )
        );
    }
}
