package io.microconfig.osdf.api.parameter;

import io.microconfig.osdf.parameters.AbstractParameter;

public class RoutingRuleParameter extends AbstractParameter<String> {
    public RoutingRuleParameter() {
        super("rule", "r", "Routing rule. Use mirror, header or <weight percentage>");
    }
}
