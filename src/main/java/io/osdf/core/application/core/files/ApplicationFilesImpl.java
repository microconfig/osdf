package io.osdf.core.application.core.files;

import io.osdf.common.exceptions.OSDFException;
import io.osdf.common.yaml.YamlObject;
import io.osdf.core.application.core.files.metadata.ApplicationMetadata;
import io.osdf.core.cluster.resource.LocalClusterResource;
import io.osdf.core.cluster.resource.LocalClusterResourceImpl;
import io.osdf.core.local.component.ComponentDir;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

import static io.osdf.common.utils.YamlUtils.createFromFile;
import static io.osdf.common.yaml.YamlObject.yaml;
import static java.nio.file.Files.list;
import static java.nio.file.Path.of;
import static java.util.stream.Collectors.toUnmodifiableList;

public class ApplicationFilesImpl implements ApplicationFiles {
    private static final String RESOURCES_DIR_NAME = "resources";

    private final ComponentDir componentDir;
    private final Path resourcesDir;

    private ApplicationFilesImpl(ComponentDir componentDir) {
        this.componentDir = componentDir;
        this.resourcesDir = componentDir.getPath(RESOURCES_DIR_NAME);
    }

    public static ApplicationFilesImpl applicationFiles(ComponentDir componentDir) {
        return new ApplicationFilesImpl(componentDir);
    }

    @Override
    public List<LocalClusterResource> resources() {
        try (Stream<Path> files = list(resourcesDir)) {
            return files
                    .map(LocalClusterResourceImpl::localClusterResource)
                    .collect(toUnmodifiableList());
        } catch (IOException e) {
            throw new OSDFException("Can't access " + resourcesDir);
        }
    }

    @Override
    public ApplicationMetadata metadata() {
        return createFromFile(ApplicationMetadata.class, of(componentDir.root() + "/osdf-metadata.yaml"));
    }

    @Override
    public YamlObject deployProperties() {
        try {
            return yaml(getPath("deploy.yaml"));
        } catch (OSDFException e) {
            throw new OSDFException("Kubernetes components must have deploy.yaml file");
        }
    }

    @Override
    public String name() {
        return componentDir.name();
    }

    @Override
    public Path root() {
        return componentDir.root();
    }

    @Override
    public Path getPath(String identifier) {
        return componentDir.getPath(identifier);
    }
}
