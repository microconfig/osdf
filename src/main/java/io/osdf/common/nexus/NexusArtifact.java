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
    private String classifier;
    private String type;

    public static NexusArtifact nexusArtifact(String group, String artifact, String version, String type) {
        return new NexusArtifact(group, artifact, version, null, type);
    }

    public static NexusArtifact configsNexusArtifact(String artifactString) {
        String[] split = artifactString.split(":");
        if (split.length != 3 && split.length != 4) throw new OSDFException("Error parsing configs nexus artifact. Should be: group:artifact:version or group:artifact:version:classifier");

        NexusArtifact artifact = nexusArtifact(split[0], split[1], split[2], "zip");
        if (split.length == 4) artifact.setClassifier(split[3]);
        return artifact;
    }

    public String getDownloadUrl(String baseUrl) {
        return baseUrl + "/" + group.replace(".", "/") + "/" + artifact + "/" + version + "/" + filename();
    }

    private String filename() {
        return artifact + "-" + version + (classifier != null ? "-" + classifier : "") + "." + type;
    }

    @Override
    public String toString() {
        return group + ":" + artifact + ":" + version + (classifier != null ? ":" + classifier : "");
    }
}
