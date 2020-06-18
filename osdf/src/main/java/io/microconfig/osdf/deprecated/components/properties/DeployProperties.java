package io.microconfig.osdf.deprecated.components.properties;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.nio.file.Path;
import java.util.Map;

import static io.microconfig.osdf.utils.YamlUtils.*;
import static java.nio.file.Path.of;

@RequiredArgsConstructor
@Getter
public class DeployProperties {
    private final String version;
    private final String type;
    private final Integer podStartTime;

    public static DeployProperties deployProperties(Path componentPath) {
        Map<String, Object> yaml = loadFromPath(of(componentPath + "/deploy.yaml"));

        String version = getString(yaml, "version");
        String imageVersion = getString(yaml, "image", "version");
        String type = getString(yaml, "component", "type");
        Integer podStartTime = getInt(yaml, "osdf", "start", "podWaitSec");
        return new DeployProperties(version != null ? version : imageVersion, type != null ? type : "default", podStartTime);
    }
}
