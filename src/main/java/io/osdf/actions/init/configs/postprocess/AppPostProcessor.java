package io.osdf.actions.init.configs.postprocess;

import io.osdf.actions.init.configs.postprocess.metadata.MetadataCreator;
import io.osdf.actions.init.configs.postprocess.types.MetadataType;
import io.osdf.common.exceptions.OSDFException;
import io.osdf.common.yaml.YamlObject;
import io.osdf.core.local.component.ComponentDir;

import java.io.IOException;
import java.nio.file.Path;
import java.util.stream.Stream;

import static io.osdf.actions.init.configs.postprocess.ConfigMapCreator.configMapCreator;
import static io.osdf.actions.init.configs.postprocess.metadata.MetadataCreatorImpl.metadataCreator;
import static io.osdf.common.utils.FileUtils.*;
import static io.osdf.common.yaml.YamlObject.yaml;
import static java.nio.file.Files.list;
import static java.util.Arrays.stream;
import static java.util.function.Predicate.not;
import static java.util.stream.Stream.of;

public class AppPostProcessor {
    private final ConfigMapCreator configMapCreator = configMapCreator();
    private final MetadataCreator metadataCreator = metadataCreator();

    public static AppPostProcessor componentPostProcessor() {
        return new AppPostProcessor();
    }

    public void process(ComponentDir componentDir) {
        splitResources(componentDir);
        configMapCreator.createConfigMaps(componentDir);
        stream(MetadataType.values()).forEach(type -> metadataCreator.create(type, componentDir));
    }

    private void splitResources(ComponentDir componentDir) {
        try (Stream<Path> resources = list(componentDir.getPath("resources"))) {
            resources.forEach(this::splitResource);
        } catch (IOException e) {
            throw new OSDFException("Couldn't list resources in " + componentDir.name());
        }
    }

    private void splitResource(Path path) {
        String content = readAll(path);
        if (!content.contains("---")) return;

        of(content.split("---"))
                .map(String::trim)
                .filter(not(String::isEmpty))
                .forEach(newFileContent -> createNewFile(path, newFileContent));
        delete(path);
    }

    private void createNewFile(Path path, String newFileContent) {
        YamlObject yaml = yaml(newFileContent);
        String kind = yaml.get("kind");
        String name = yaml.get("metadata.name");
        Path newPath = Path.of(path.toString().replace(".yaml", "-" + kind + "-" + name + ".yaml"));
        writeStringToFile(newPath, newFileContent);
    }
}
