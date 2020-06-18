package io.microconfig.osdf.api.parameter;

import io.microconfig.osdf.parameters.ArgParameter;

public class RoutingRuleParameter extends ArgParameter<String> {
    public RoutingRuleParameter() {
        super("rule", "r", "Routing rule. Use mirror, header or <weight percentage>");
    }
}
