package io.osdf.api.parsers;

import io.osdf.api.lib.argparsers.ArgParser;
import io.osdf.common.nexus.NexusArtifact;

import static io.osdf.common.nexus.NexusArtifact.configsNexusArtifact;

public class NexusArtifactParser implements ArgParser<NexusArtifact> {
    @Override
    public NexusArtifact parse(String arg) {
        if (arg == null) return null;
        return configsNexusArtifact(arg);
    }
}
