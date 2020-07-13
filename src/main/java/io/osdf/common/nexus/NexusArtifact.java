package io.osdf.common.nexus;

import io.osdf.common.exceptions.OSDFException;
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

    public static NexusArtifact configsNexusArtifact(String artifactString) {
        String[] split = artifactString.split(":");
        if (split.length != 3) throw new OSDFException("Error parsing configs nexus artifact. Should be: group:artifact:version");
        return nexusArtifact(split[0], split[1], split[2], "zip");
    }

    public String getDownloadUrl(String baseUrl) {
        return baseUrl + "/" + group.replace(".", "/") + "/" + artifact + "/" + version + "/" + artifact + "-" + version + "." + type;
    }

    @Override
    public String toString() {
        return group + "." + artifact + "." + version;
    }
}
