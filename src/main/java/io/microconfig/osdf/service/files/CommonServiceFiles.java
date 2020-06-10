package io.microconfig.osdf.service.files;

import io.microconfig.osdf.cluster.resource.LocalClusterResource;
import io.microconfig.osdf.cluster.resource.LocalClusterResourceImpl;
import io.microconfig.osdf.component.ComponentDir;
import io.microconfig.osdf.exceptions.OSDFException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

import static java.nio.file.Files.list;
import static java.util.stream.Collectors.toUnmodifiableList;

public class CommonServiceFiles implements ServiceFiles {
    private final ComponentDir componentDir;
    private final Path resourcesDir;

    CommonServiceFiles(ComponentDir componentDir, String type) {
        this.componentDir = componentDir;
        this.resourcesDir = componentDir.getPath(type);
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
    public List<Path> configs() {
        try (Stream<Path> files = list(componentDir.root())) {
            return files.filter(Files::isRegularFile)
                    .filter(file -> !file.getFileName().toString().contains("diff-"))
                    .filter(file -> !file.getFileName().toString().contains("secret"))
                    .collect(toUnmodifiableList());
        } catch (Exception e) {
            throw new OSDFException("Can't get config files " + componentDir);
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
        return componentDir.getPath("common");
    }
}
