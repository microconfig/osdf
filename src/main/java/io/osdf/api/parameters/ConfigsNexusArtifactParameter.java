package io.osdf.api.parameters;

import io.osdf.common.nexus.NexusArtifact;
import io.osdf.api.lib.parameter.ArgParameter;

import static io.osdf.common.nexus.NexusArtifact.configsNexusArtifact;

public class ConfigsNexusArtifactParameter extends ArgParameter<NexusArtifact> {
    public ConfigsNexusArtifactParameter() {
        super("artifact", "a", "Nexus artifact for configs. Format: group:artifact:version");
    }

    @Override
    public NexusArtifact get() {
        if (getValue() == null) return null;
        return configsNexusArtifact(getValue());
    }
}
