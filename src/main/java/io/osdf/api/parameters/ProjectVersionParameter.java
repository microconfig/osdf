package io.osdf.api.parameters;

import io.osdf.api.lib.parameter.ArgParameter;

public class ProjectVersionParameter extends ArgParameter<String> {
    public ProjectVersionParameter() {
        super("projVersion", "pv", "Version of project. Corresponds to docker image version");
    }
}
