package io.microconfig.osdf.api.parameter;

import io.microconfig.osdf.nexus.NexusArtifact;
import io.microconfig.osdf.parameters.AbstractParameter;

import static io.microconfig.osdf.nexus.NexusArtifact.configsNexusArtifact;

public class ConfigsNexusArtifactParameter extends AbstractParameter<NexusArtifact> {
    public ConfigsNexusArtifactParameter() {
        super("configsNexusArtifact", "ca", "Nexus artifact for configs. Format: group:artifact");
    }

    @Override
    public NexusArtifact get() {
        if (getValue() == null) return null;
        return configsNexusArtifact(getValue());
    }
}
