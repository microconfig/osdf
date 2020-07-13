package io.osdf.api.parameters;

import io.osdf.api.lib.parameter.ArgParameter;

public class RoutingRuleParameter extends ArgParameter<String> {
    public RoutingRuleParameter() {
        super("rule", "r", "Routing rule. Use mirror, header or <weight percentage>");
    }
}
