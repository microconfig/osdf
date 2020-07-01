package unstable.io.osdf.istio;

import unstable.io.osdf.istio.rules.HeaderRule;
import unstable.io.osdf.istio.rules.MainRule;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static unstable.io.osdf.istio.rules.MainRule.fromYaml;
import static java.lang.String.join;
import static java.util.stream.Collectors.toList;

@AllArgsConstructor
public class RuleSet {
    @Getter
    @Setter
    private MainRule mainRule;
    private List<HeaderRule> headerRules;

    public static RuleSet from(List<Object> rules) {
        MainRule mainRule = fromYaml(rules.get(rules.size() - 1));
        List<HeaderRule> headerRules = rules.stream()
                .map(RuleSet::parseHeaderRule)
                .filter(Objects::nonNull)
                .collect(toList());
        return new RuleSet(mainRule, headerRules);
    }

    private static HeaderRule parseHeaderRule(Object ruleObj) {
        try {
            return HeaderRule.fromYaml(ruleObj);
        } catch (Exception ignored) {
            return null;
        }
    }

    public void addHeaderRule(HeaderRule headerRule) {
        headerRules.add(headerRule);
    }

    public void deleteHeaderRule(String subset) {
        headerRules.removeIf(r -> r.getDestination().getSubset().equals(subset));
    }

    public String getTrafficStatus(String subset) {
        List<String> types = new ArrayList<>();
        int weight = mainRule.getWeight(subset);
        if (weight > 0) {
            types.add(weight + "%");
        }
        if (subset.equals(mainRule.mirrorSubset())) {
            types.add("mirror");
        }
        if (headerRules.stream().anyMatch(headerRule -> headerRule.getDestination().getSubset().equals(subset))) {
            types.add("header");
        }
        String status = join(",", types);
        return status.length() > 0 ? status : "-";
    }

    public List<Object> toYaml() {
        List<Object> rules = headerRules.stream().map(HeaderRule::toYaml).collect(toList());
        rules.add(mainRule.toYaml());
        return rules;
    }
}
