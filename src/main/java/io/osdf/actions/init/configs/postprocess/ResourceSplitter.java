package io.osdf.actions.init.configs.postprocess;

import io.osdf.common.exceptions.OSDFException;
import io.osdf.common.yaml.YamlObject;
import io.osdf.core.local.component.ComponentDir;

import java.io.IOException;
import java.nio.file.Path;
import java.util.stream.Stream;

import static io.osdf.common.utils.FileUtils.*;
import static io.osdf.common.yaml.YamlObject.yaml;
import static java.nio.file.Files.list;
import static java.util.function.Predicate.not;
import static java.util.stream.Stream.of;
import static org.apache.commons.io.FilenameUtils.getExtension;

public class ResourceSplitter {
    public static ResourceSplitter resourceSplitter() {
        return new ResourceSplitter();
    }

    public void splitResources(ComponentDir componentDir) {
        try (Stream<Path> resources = list(componentDir.getPath("resources"))) {
            resources.forEach(this::splitResource);
        } catch (IOException e) {
            throw new OSDFException("Couldn't list resources in " + componentDir.name());
        }
    }

    private void splitResource(Path path) {
        String content = readAll(path);
        boolean containsSplitter = of(content.split("\n"))
                .anyMatch(line -> line.trim().equals("---"));
        if (!containsSplitter) return;

        of(content.split("^---$"))
                .map(String::trim)
                .filter(not(String::isEmpty))
                .forEach(newFileContent -> createNewFile(path, newFileContent));
        delete(path);
    }

    private void createNewFile(Path path, String newFileContent) {
        YamlObject yaml = yaml(newFileContent);
        String kind = yaml.get("kind");
        String name = yaml.get("metadata.name");

        String extension = getExtension(path.toString());
        Path newPath = Path.of(path.toString().replace("." + extension, "-" + kind + "-" + name + "." + extension));
        writeStringToFile(newPath, newFileContent);
    }
}
