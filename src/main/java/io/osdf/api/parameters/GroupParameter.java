package io.osdf.api.parameters;

import io.osdf.api.lib.parameter.ArgParameter;

public class GroupParameter extends ArgParameter<String> {
    public GroupParameter() {
        super("group", "gr", "Microconfig components group");
    }
}
