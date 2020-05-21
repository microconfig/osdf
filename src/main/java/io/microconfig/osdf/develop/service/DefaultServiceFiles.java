package io.microconfig.osdf.develop.service;

import io.microconfig.osdf.develop.cluster.resource.LocalClusterResource;
import io.microconfig.osdf.develop.cluster.resource.LocalClusterResourceImpl;
import io.microconfig.osdf.develop.component.ComponentDir;
import io.microconfig.osdf.exceptions.OSDFException;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

import static java.nio.file.Files.exists;
import static java.nio.file.Files.list;
import static java.util.stream.Collectors.toUnmodifiableList;

@RequiredArgsConstructor
public class DefaultServiceFiles implements ServiceFiles {
    private final ComponentDir componentDir;
    private final Path resourcesDir;

    public static DefaultServiceFiles serviceFiles(ComponentDir componentDir) {
        if (exists(componentDir.getPath("resources"))) {
            return new DefaultServiceFiles(componentDir, componentDir.getPath("resources"));
        }
        if (exists(componentDir.getPath("openshift"))) {
            return new DefaultServiceFiles(componentDir, componentDir.getPath("openshift"));
        }
        throw new OSDFException("Unknown component dir format for service");
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
        return componentDir.getPath(identifier);
    }
}
