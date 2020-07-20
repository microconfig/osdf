package io.osdf.core.application.core.description;

import io.osdf.common.yaml.YamlObject;
import io.osdf.core.application.core.files.ApplicationFiles;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

import static java.util.stream.Collectors.toUnmodifiableList;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CoreDescription {
    private String appVersion;
    private String configVersion;
    private List<String> resources;
    private String type;

    public static CoreDescription from(ApplicationFiles files) {
        YamlObject yaml = files.deployProperties();
        String appVersion = yaml.get("app.version");
        String configVersion = yaml.get("config.version");
        List<String> resources = files.resources().stream()
                .map(resource -> resource.kind() + "/" + resource.name())
                .collect(toUnmodifiableList());
        return new CoreDescription(appVersion, configVersion,
                resources, files.metadata().getType());
    }
}
