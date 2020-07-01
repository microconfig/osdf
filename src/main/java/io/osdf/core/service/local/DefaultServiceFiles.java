package io.osdf.core.service.local;

import io.osdf.core.cluster.resource.LocalClusterResource;
import io.osdf.core.cluster.resource.LocalClusterResourceImpl;
import io.osdf.core.local.component.ComponentDir;
import io.osdf.common.exceptions.OSDFException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

import static java.nio.file.Files.exists;
import static java.nio.file.Files.list;
import static java.util.stream.Collectors.toUnmodifiableList;

public class DefaultServiceFiles implements ServiceFiles {
    private static final String RESOURCES_DIR_NAME = "resources";

    private final ComponentDir componentDir;
    private final Path resourcesDir;

    private DefaultServiceFiles(ComponentDir componentDir) {
        this.componentDir = componentDir;
        this.resourcesDir = componentDir.getPath(RESOURCES_DIR_NAME);
    }

    public static DefaultServiceFiles serviceFiles(ComponentDir componentDir) {
        return new DefaultServiceFiles(componentDir);
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
                    .filter(file -> !file.getFileName().toString().equals("deploy.yaml"))
                    .filter(file -> !file.getFileName().toString().equals("process.properties"))
                    .filter(file -> !file.getFileName().toString().contains("diff-"))
                    .filter(file -> !file.getFileName().toString().contains("secret"))
                    .collect(toUnmodifiableList());
        } catch (IOException e) {
            throw new OSDFException("Can't access component dit of " + componentDir.name());
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
        if (identifier.equals("mainResource")) {
            if (exists(getPath(RESOURCES_DIR_NAME + "/deployment.yaml")))
                return getPath(RESOURCES_DIR_NAME + "/deployment.yaml");
            if (exists(getPath(RESOURCES_DIR_NAME + "/job.yaml")))
                return getPath(RESOURCES_DIR_NAME + "/job.yaml");
            throw new OSDFException("Main resource is not found");
        }
        return componentDir.getPath(identifier);
    }
}
