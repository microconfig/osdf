package io.microconfig.osdf.components.loader;

import io.microconfig.osdf.utils.YamlUtils;
import lombok.RequiredArgsConstructor;
import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.util.Map;

@RequiredArgsConstructor
public class ComponentDeployProperties {
    private final Map<String, Object> config;

    public static ComponentDeployProperties deployProperties(Path componentPath) {
        try {
            return new ComponentDeployProperties(new Yaml().load(new FileInputStream(Path.of(componentPath + "/deploy.yaml").toString())));
        } catch (FileNotFoundException e) {
            return new ComponentDeployProperties(Map.of());
        }
    }

    public String getOrNull(String... properties) {
        return YamlUtils.getString(config, properties);
    }

    public String get(String... properties) {
       return YamlUtils.getString(config, properties);
    }
}
