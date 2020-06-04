package io.microconfig.osdf.chaos.components;

import io.microconfig.osdf.component.ComponentDir;
import io.microconfig.osdf.exceptions.OSDFException;
import lombok.RequiredArgsConstructor;

import java.nio.file.Path;
import java.nio.file.Paths;

import static java.nio.file.Files.exists;

@RequiredArgsConstructor
public class DefaultChaosComponent implements ChaosComponent {
    private final Path pathToPlan;

    public static ChaosComponent chaosFiles(ComponentDir componentDir) {
        if (exists(componentDir.getPath("application.yaml"))) {
            return new DefaultChaosComponent(Paths.get(componentDir.root() + "/application.yaml"));
        }
        throw new OSDFException("Can't access chaos plan " + componentDir.name());
    }

    @Override
    public Path getPathToPlan() {
        return pathToPlan;
    }
}