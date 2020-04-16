package io.microconfig.osdf.nexus;

import io.microconfig.osdf.exceptions.OSDFException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class NexusArtifact {
    private String group;
    private String artifact;
    private String version;
    private String type;

    public static NexusArtifact nexusArtifact(String group, String artifact, String version, String type) {
        return new NexusArtifact(group, artifact, version, type);
    }

    public static NexusArtifact configsNexusArtifact(String groupAndArtifact) {
        String[] split = groupAndArtifact.split(":");
        if (split.length != 2) throw new OSDFException("Error parsing configs nexus artifact. Should be: group:artifact");
        return nexusArtifact(split[0], split[1], "1.0", "zip");
    }

    public String getDownloadUrl(String baseUrl) {
        return baseUrl + "/" + group.replace(".", "/") + "/" + artifact + "/" + version + "/" + artifact + "-" + version + "." + type;
    }
}
