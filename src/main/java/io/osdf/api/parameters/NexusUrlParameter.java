package io.osdf.api.parameters;

import io.osdf.api.lib.parameter.ArgParameter;

public class NexusUrlParameter extends ArgParameter<String> {
    public NexusUrlParameter() {
        super("url", "u", "Nexus url");
    }
}
