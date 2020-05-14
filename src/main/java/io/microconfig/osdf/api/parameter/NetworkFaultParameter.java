package io.microconfig.osdf.api.parameter;

import io.microconfig.osdf.parameters.ArgParameter;

public class NetworkFaultParameter extends ArgParameter<String> {
    public NetworkFaultParameter() {
        super("fault", "f", "Type of network fault: delay or abort");
    }
}
