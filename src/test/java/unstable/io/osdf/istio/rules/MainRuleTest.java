package unstable.io.osdf.istio.rules;

import unstable.io.osdf.istio.Destination;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import unstable.io.osdf.istio.WeightRoute;

import java.util.List;
import java.util.Map;

import static unstable.io.osdf.istio.Destination.destination;
import static unstable.io.osdf.istio.rules.MainRule.fromYaml;
import static io.osdf.common.utils.YamlUtils.getList;
import static io.osdf.common.utils.YamlUtils.getMap;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class MainRuleTest {
    private final Destination first = destination("host", "v1");
    private final Destination second = destination("host", "v2");
    private Map<String, Object> yaml;

    @BeforeEach
    void setUp() {
        yaml = Map.of(
                "route", List.of(
                        WeightRoute.weightRoute(first, 95).toYaml(),
                        WeightRoute.weightRoute(second, 5).toYaml()
                )
        );
    }

    @Test
    @SuppressWarnings("unchecked")
    void testSerialization() {
        Map<String, Object> result = (Map<String, Object>) fromYaml(yaml).toYaml();
        assertEquals(getList(yaml, "route"), getList(result, "route"));
    }

    @Test
    @SuppressWarnings("unchecked")
    void testSetWeight() {
        MainRule rule = fromYaml(yaml);
        rule.setWeight("v2", 10);
        Map<String, Object> result = (Map<String, Object>) rule.toYaml();

        assertEquals(10, rule.getWeight("v2"));
        Assertions.assertEquals(List.of(
                WeightRoute.weightRoute(first, 90).toYaml(),
                WeightRoute.weightRoute(second, 10).toYaml()
        ), getList(result, "route"));
    }

    @Test
    @SuppressWarnings("unchecked")
    void testSetWeight100() {
        MainRule rule = fromYaml(yaml);
        rule.setWeight("v2", 100);
        Map<String, Object> result = (Map<String, Object>) rule.toYaml();

        Assertions.assertEquals(
                List.of(WeightRoute.weightRoute(second, 100).toYaml()),
                getList(result, "route")
        );
    }

    @Test
    @SuppressWarnings("unchecked")
    void testSetMirror() {
        MainRule rule = fromYaml(yaml);
        rule.setMirror("v2");
        Map<String, Object> result = (Map<String, Object>) rule.toYaml();

        assertEquals("v2", rule.mirrorSubset());
        Assertions.assertEquals(
                List.of(WeightRoute.weightRoute(first, 100).toYaml()),
                getList(result, "route")
        );
        assertEquals(second.toYaml(), getMap(result, "mirror"));
    }

    @Test
    @SuppressWarnings("unchecked")
    void testSetWeightRemovesMirror() {
        Map<String, Object> withMirror = Map.of(
                "route", List.of(WeightRoute.weightRoute(first, 100).toYaml()
                ),
                "mirror", second.toYaml()
        );

        MainRule rule = fromYaml(withMirror);
        rule.setWeight("v2", 5);
        Map<String, Object> result = (Map<String, Object>) rule.toYaml();

        assertEquals(getList(yaml, "route"), getList(result, "route"));
        assertNull(getMap(result, "mirror"));
    }

    @Test
    @SuppressWarnings("unchecked")
    void testDeleteSubset() {
        MainRule rule = fromYaml(yaml);
        rule.deleteSubset("v1");
        Map<String, Object> result = (Map<String, Object>) rule.toYaml();

        Assertions.assertEquals(
                List.of(WeightRoute.weightRoute(second, 100).toYaml()),
                getList(result, "route")
        );
    }
}