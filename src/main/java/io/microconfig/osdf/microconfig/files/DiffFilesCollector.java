package io.microconfig.osdf.microconfig.files;

import io.microconfig.osdf.exceptions.OSDFException;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

import static java.nio.file.Files.isDirectory;
import static java.nio.file.Files.walk;
import static java.util.stream.Collectors.toUnmodifiableList;

@RequiredArgsConstructor
public class DiffFilesCollector {
    private final Path root;

    public static DiffFilesCollector collector(Path root) {
        return new DiffFilesCollector(root);
    }

    public List<Path> collect() {
        try (Stream<Path> files = walk(root)) {
            return files
                    .filter(p -> !isDirectory(p))
                    .filter(p -> p.getFileName().toString().startsWith("diff-"))
                    .collect(toUnmodifiableList());
        } catch (IOException e) {
            throw new OSDFException("Can't collect diff files in " + root, e);
        }
    }
}
