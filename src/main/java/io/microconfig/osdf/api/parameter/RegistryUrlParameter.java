package io.microconfig.osdf.api.parameter;

import io.microconfig.osdf.parameters.ArgParameter;

public class RegistryUrlParameter extends ArgParameter<String> {
    public RegistryUrlParameter() {
        super("url", "u", "Registry host");
    }
}
