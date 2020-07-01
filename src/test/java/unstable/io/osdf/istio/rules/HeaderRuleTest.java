package unstable.io.osdf.istio.rules;

import org.junit.jupiter.api.Test;
import org.mockito.internal.matchers.apachecommons.ReflectionEquals;

import java.util.List;
import java.util.Map;

import static unstable.io.osdf.istio.Destination.destination;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HeaderRuleTest {
    @Test
    void testSerialization() {
        Map<String, Object> yaml = Map.of(
                "route", List.of(
                        Map.of(
                                "destination", destination("host", "subset").toYaml()
                        )
                ),
                "match", List.of(
                        Map.of(
                                "headers",
                                Map.of(
                                        "headerName",
                                        Map.of(
                                                "exact",
                                                "headerValue"
                                        )
                                )
                        )
                )
        );

        HeaderRule rule = HeaderRule.fromYaml(yaml);
        assertEquals("host", rule.getDestination().getHost());
        assertEquals("subset", rule.getDestination().getSubset());

        assertTrue(new ReflectionEquals(yaml).matches(rule.toYaml()));
    }
}