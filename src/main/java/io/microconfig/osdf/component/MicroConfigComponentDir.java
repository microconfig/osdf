package io.microconfig.osdf.component;

import lombok.RequiredArgsConstructor;

import java.nio.file.Path;

import static java.nio.file.Path.of;

@RequiredArgsConstructor
public class MicroConfigComponentDir implements ComponentDir {
    private final Path root;

    public static MicroConfigComponentDir componentDir(Path root) {
        return new MicroConfigComponentDir(root);
    }

    @Override
    public String name() {
        return root.getFileName().toString();
    }

    @Override
    public Path root() {
        return root;
    }

    @Override
    public Path getPath(String identifier) {
        return of(root + "/" + identifier);
    }
}
