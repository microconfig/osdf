package unstable.io.osdf.istio;

import org.junit.jupiter.api.Test;
import org.mockito.internal.matchers.apachecommons.ReflectionEquals;

import java.util.Map;

import static unstable.io.osdf.istio.Destination.fromYaml;
import static java.util.Map.of;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DestinationTest {
    @Test
    void testSerialization() {
        Map<String, Object> yaml = of("host", "someHost", "subset", "someSubset");

        Destination destination = fromYaml(yaml);
        assertEquals("someHost", destination.getHost());
        assertEquals("someSubset", destination.getSubset());

        assertTrue(new ReflectionEquals(yaml).matches(destination.toYaml()));

    }
}