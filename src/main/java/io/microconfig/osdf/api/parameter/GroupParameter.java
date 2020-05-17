package io.microconfig.osdf.api.parameter;

import io.microconfig.osdf.parameters.ArgParameter;

public class GroupParameter extends ArgParameter<String> {
    public GroupParameter() {
        super("group", "gr", "Microconfig components group");
    }
}
