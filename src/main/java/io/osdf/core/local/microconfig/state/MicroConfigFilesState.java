package io.osdf.core.local.microconfig.state;

import io.osdf.common.exceptions.OSDFException;
import io.osdf.common.utils.FileUtils;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.stream.Stream;

import static java.nio.file.Files.*;
import static java.util.Comparator.comparingInt;
import static java.util.stream.Collectors.toSet;

@RequiredArgsConstructor
public class MicroConfigFilesState {
    private final Set<MicroConfigFile> files;

    public static MicroConfigFilesState of(Path root) {
        return new MicroConfigFilesState(collectFiles(root));
    }

    public void clearUnchanged(MicroConfigFilesState newState) {
        deleteUnchangedFiles(newState);
        deleteEmptyFolders(newState);
    }

    private void deleteUnchangedFiles(MicroConfigFilesState newState) {
        files.stream()
                .filter(file -> !isDirectory(file.getPath()))
                .filter(newState.files::contains)
                .forEach(MicroConfigFile::delete);
    }

    private void deleteEmptyFolders(MicroConfigFilesState newState) {
        newState.files.stream()
                .map(MicroConfigFile::getPath)
                .filter(Files::isDirectory)
                .sorted(comparingInt(p -> -p.toString().length()))
                .forEach(FileUtils::deleteIfEmpty);
    }

    private static Set<MicroConfigFile> collectFiles(Path root) {
        try (Stream<Path> files = walk(root)) {
            return files
                    .filter(p -> !p.equals(root))
                    .map(MicroConfigFile::of)
                    .collect(toSet());
        } catch (IOException e) {
            throw new OSDFException("Error accessing files in " + root);
        }
    }
}
