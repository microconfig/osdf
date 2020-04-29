package io.microconfig.osdf.api.parameter;

import io.microconfig.osdf.nexus.NexusArtifact;
import io.microconfig.osdf.parameters.ArgParameter;

import static io.microconfig.osdf.nexus.NexusArtifact.configsNexusArtifact;

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
