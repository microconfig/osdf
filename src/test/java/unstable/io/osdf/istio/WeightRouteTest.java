package unstable.io.osdf.istio;

import org.junit.jupiter.api.Test;
import org.mockito.internal.matchers.apachecommons.ReflectionEquals;

import java.util.Map;

import static unstable.io.osdf.istio.Destination.destination;
import static java.util.Map.of;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class WeightRouteTest {
    @Test
    void testSerialization() {
        Map<String, Object> yaml = of(
                "destination", destination("host", "subset").toYaml(),
                "weight", 19
        );

        WeightRoute route = WeightRoute.fromYaml(yaml);
        assertEquals("host", route.getDestination().getHost());
        assertEquals("subset", route.getDestination().getSubset());
        assertEquals(19, route.getWeight());

        assertTrue(new ReflectionEquals(yaml).matches(route.toYaml()));
    }
}