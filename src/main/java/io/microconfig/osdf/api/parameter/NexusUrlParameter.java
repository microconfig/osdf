package io.microconfig.osdf.api.parameter;

import io.microconfig.osdf.parameters.AbstractParameter;

public class NexusUrlParameter extends AbstractParameter<String> {
    public NexusUrlParameter() {
        super("nexusUrl", "n", "Nexus url");
    }
}
