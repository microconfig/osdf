package io.osdf.common.yaml;

import lombok.RequiredArgsConstructor;
import org.yaml.snakeyaml.Yaml;

import java.nio.file.Path;
import java.util.Map;

import static io.osdf.common.utils.YamlUtils.getObjectOrNull;
import static io.osdf.common.utils.YamlUtils.loadFromPath;

@RequiredArgsConstructor
public class YamlObject {
    private final Map<String, Object> yaml;

    public static YamlObject yaml(Path path) {
        return new YamlObject(loadFromPath(path));
    }

    @SuppressWarnings("unchecked")
    public static YamlObject yaml(Object obj) {
        return new YamlObject((Map<String, Object>) obj);
    }

    public static YamlObject yaml(String content) {
        return new YamlObject(new Yaml().load(content));
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String key) {
        return (T) getObjectOrNull(yaml, key.split("\\."));
    }
}
