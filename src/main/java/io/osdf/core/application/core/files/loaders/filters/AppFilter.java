package io.osdf.core.application.core.files.loaders.filters;

import io.osdf.core.local.component.ComponentDir;

import java.nio.file.Path;
import java.util.function.Predicate;

import static io.osdf.common.yaml.YamlObject.yaml;
import static java.nio.file.Files.exists;

public class AppFilter implements Predicate<ComponentDir> {
    public static AppFilter isApp() {
        return new AppFilter();
    }

    @Override
    public boolean test(ComponentDir componentDir) {
        Path deployYamlPath = componentDir.getPath("deploy.yaml");
        if (!exists(deployYamlPath)) return false;
        if (exists(componentDir.getPath("resources"))) return true;

        return yaml(deployYamlPath).get("osdf.configmap") != null;
    }
}
