package io.microconfig.osdf.api.parameter;

import io.microconfig.osdf.parameters.ArgParameter;

public class ProjectVersionParameter extends ArgParameter<String> {
    public ProjectVersionParameter() {
        super("projVersion", "pv", "Version of project. Corresponds to docker image version");
    }
}
