package io.microconfig.osdf.api.parameter;

import io.microconfig.osdf.parameters.AbstractParameter;

public class GroupParameter extends AbstractParameter<String> {
    public GroupParameter() {
        super("group", "gr", "Microconfig components group");
    }
}
