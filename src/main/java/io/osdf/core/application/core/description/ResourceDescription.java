package io.osdf.core.application.core.description;

import io.osdf.common.yaml.YamlObject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.nio.file.Path;

import static io.osdf.common.yaml.YamlObject.yaml;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResourceDescription {
    private String kind;
    private String name;

    public static ResourceDescription fromLocalResource(Path path) {
        YamlObject deployment = yaml(path);
        String kind = deployment.get("kind");
        String name = deployment.get("metadata.name");
        return new ResourceDescription(kind.toLowerCase(), name);
    }
}
