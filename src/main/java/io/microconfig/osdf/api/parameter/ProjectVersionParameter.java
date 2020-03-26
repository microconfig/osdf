package io.microconfig.osdf.api.parameter;

import io.microconfig.osdf.parameters.AbstractParameter;

public class ProjectVersionParameter extends AbstractParameter<String> {
    public ProjectVersionParameter() {
        super("projVersion", "pv", "Version of project. Corresponds to docker image version");
    }
}
