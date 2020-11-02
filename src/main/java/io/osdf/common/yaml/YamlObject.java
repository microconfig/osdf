package io.osdf.common.yaml;

import io.osdf.common.exceptions.PossibleBugException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.yaml.snakeyaml.Yaml;

import java.nio.file.Path;
import java.util.Map;

import static io.osdf.common.utils.YamlUtils.getObjectOrNull;
import static io.osdf.common.utils.YamlUtils.loadFromPath;
import static java.lang.String.valueOf;

@RequiredArgsConstructor
public class YamlObject {
    @Getter
    private final Map<String, Object> yaml;

    public static YamlObject yaml(Path path) {
        return new YamlObject(loadFromPath(path));
    }

    @SuppressWarnings("unchecked")
    public static YamlObject yaml(Object obj) {
        return new YamlObject((Map<String, Object>) obj);
    }

    public static YamlObject yaml(String content) {
        try {
            return new YamlObject(new Yaml().load(content));
        } catch (RuntimeException e) {
            throw new PossibleBugException("Couldn't parse yaml content", e);
        }
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String key) {
        return (T) getObjectOrNull(yaml, key.split("\\."));
    }

    public String getString(String key) {
        return valueOf(getObjectOrNull(yaml, key.split("\\.")));
    }
}
