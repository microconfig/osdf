package io.microconfig.osdf.istio;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Map;

import static io.microconfig.osdf.utils.YamlUtils.getString;
import static java.util.Map.of;

@RequiredArgsConstructor
@Getter
public class Destination {
    private final String host;
    private final String subset;

    public static Destination destination(String host, String subset) {
        return new Destination(host, subset);
    }

    @SuppressWarnings("unchecked")
    public static Destination fromYaml(Object destinationObject) {
        Map<String, Object> destination = (Map<String, Object>) destinationObject;

        String host = getString(destination, "host");
        String subset = getString(destination, "subset");
        return new Destination(host, subset);
    }

    public Object toYaml() {
        return of("host", host, "subset", subset);
    }
}
