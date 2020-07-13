package io.osdf.api.parameters;

import io.osdf.api.lib.parameter.ArgParameter;

public class RegistryUrlParameter extends ArgParameter<String> {
    public RegistryUrlParameter() {
        super("url", "u", "Registry host");
    }
}
