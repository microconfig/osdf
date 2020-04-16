package io.microconfig.osdf.api.parameter;

import io.microconfig.osdf.parameters.ArgParameter;

public class NexusUrlParameter extends ArgParameter<String> {
    public NexusUrlParameter() {
        super("nexusUrl", "n", "Nexus url");
    }
}
