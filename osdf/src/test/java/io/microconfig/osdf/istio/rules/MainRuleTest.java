package io.microconfig.osdf.istio.rules;

import io.microconfig.osdf.istio.Destination;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static io.microconfig.osdf.istio.Destination.destination;
import static io.microconfig.osdf.istio.WeightRoute.weightRoute;
import static io.microconfig.osdf.istio.rules.MainRule.fromYaml;
import static io.microconfig.osdf.utils.YamlUtils.getList;
import static io.microconfig.osdf.utils.YamlUtils.getMap;
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
                        weightRoute(first, 95).toYaml(),
                        weightRoute(second, 5).toYaml()
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
        assertEquals(List.of(
                weightRoute(first, 90).toYaml(),
                weightRoute(second, 10).toYaml()
        ), getList(result, "route"));
    }

    @Test
    @SuppressWarnings("unchecked")
    void testSetWeight100() {
        MainRule rule = fromYaml(yaml);
        rule.setWeight("v2", 100);
        Map<String, Object> result = (Map<String, Object>) rule.toYaml();

        assertEquals(
                List.of(weightRoute(second, 100).toYaml()),
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
        assertEquals(
                List.of(weightRoute(first, 100).toYaml()),
                getList(result, "route")
        );
        assertEquals(second.toYaml(), getMap(result, "mirror"));
    }

    @Test
    @SuppressWarnings("unchecked")
    void testSetWeightRemovesMirror() {
        Map<String, Object> withMirror = Map.of(
                "route", List.of(weightRoute(first, 100).toYaml()
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

        assertEquals(
                List.of(weightRoute(second, 100).toYaml()),
                getList(result, "route")
        );
    }
}