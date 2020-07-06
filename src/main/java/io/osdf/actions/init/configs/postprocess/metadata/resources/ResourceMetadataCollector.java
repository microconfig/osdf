package io.osdf.actions.init.configs.postprocess.metadata.resources;

import io.osdf.common.exceptions.OSDFException;
import io.osdf.common.yaml.YamlObject;
import io.osdf.core.service.metadata.LocalResourceMetadata;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

import static io.osdf.common.yaml.YamlObject.yaml;
import static io.osdf.core.service.metadata.LocalResourceMetadata.create;
import static java.nio.file.Files.list;
import static java.util.stream.Collectors.toUnmodifiableList;

public class ResourceMetadataCollector {
    public static ResourceMetadataCollector resourceMetadataCollector() {
        return new ResourceMetadataCollector();
    }

    public List<LocalResourceMetadata> collect(Path resourcesDir) {
        try (Stream<Path> resources = list(resourcesDir)) {
            return resources.map(this::createLocalResourceMetadata)
                    .collect(toUnmodifiableList());
        } catch (IOException e) {
            throw new OSDFException("Error collecting resource metadata in " + resourcesDir, e);
        }
    }

    private LocalResourceMetadata createLocalResourceMetadata(Path pathToResource) {
        YamlObject yaml = yaml(pathToResource);
        String kind = yaml.get("kind");
        String name = yaml.get("metadata.name");
        return create(kind, name, pathToResource.toString());
    }
}
