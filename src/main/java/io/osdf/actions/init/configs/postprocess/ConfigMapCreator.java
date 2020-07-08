package io.osdf.actions.init.configs.postprocess;

import io.osdf.common.yaml.YamlObject;
import io.osdf.core.local.component.ComponentDir;
import lombok.RequiredArgsConstructor;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.osdf.common.utils.FileUtils.readAll;
import static io.osdf.common.utils.YamlUtils.dump;
import static io.osdf.common.yaml.YamlObject.yaml;
import static java.nio.file.Path.of;

@RequiredArgsConstructor
public class ConfigMapCreator {
    public static ConfigMapCreator configMapCreator() {
        return new ConfigMapCreator();
    }

    public void createConfigMaps(ComponentDir componentDir) {
        Map<String, Object> configMaps = yaml(componentDir.getPath("deploy.yaml"))
                .get("osdf.configmap");
        if (configMaps == null) return;
        configMaps.forEach((name, description) -> createConfigMap(componentDir, yaml(description)));
    }

    private void createConfigMap(ComponentDir componentDir, YamlObject description) {
        String name = description.get("name");
        List<String> files = description.get("files");

        Map<String, String> filesMap = new HashMap<>();
        files.forEach(file -> {
            Path path = of(componentDir.root() + "/" + file);
            String content = readAll(path);
            String fileName = path.getFileName().toString();
            filesMap.put(fileName, content);
        });

        Map<String, Object> configMap = Map.of(
                "apiVersion", "v1",
                "kind", "ConfigMap",
                "metadata", Map.of(
                        "name", name
                ),
                "data", filesMap
        );
        dump(configMap, of(componentDir.getPath("resources") + "/configmap-" + name + ".yaml"));
    }
}