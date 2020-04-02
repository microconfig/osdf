package io.microconfig.osdf.istio;

import io.microconfig.osdf.istio.rules.HeaderRule;
import io.microconfig.osdf.istio.rules.MainRule;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

import static io.microconfig.osdf.istio.rules.MainRule.fromYaml;
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
        List<HeaderRule> headerRules = new ArrayList<>();
        for (Object ruleObj : rules) {
            try {
                headerRules.add(HeaderRule.fromYaml(ruleObj));
            } catch (Exception ignored) {
            }
        }
        return new RuleSet(mainRule, headerRules);
    }

    public void addHeaderRule(HeaderRule headerRule) {
        headerRules.add(headerRule);
    }

    public void deleteHeaderRule(String subset) {
        headerRules = headerRules
                .stream()
                .filter(r -> !r.getDestination().getSubset().equals(subset))
                .collect(toList());
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
        for (HeaderRule headerRule : headerRules) {
            if (headerRule.getDestination().getSubset().equals(subset)) {
                types.add("header");
            }
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
