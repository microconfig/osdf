package io.microconfig.osdf.microconfig.files;

import io.microconfig.osdf.exceptions.OSDFException;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;

import static java.nio.file.Files.readAttributes;

@RequiredArgsConstructor
@EqualsAndHashCode
public class MicroConfigFile {
    @Getter
    private final Path path;
    private final FileTime creationTime;
    private final FileTime lastModifiedTime;

    public static MicroConfigFile of(Path path) {
        try {
            BasicFileAttributes attrs = readAttributes(path, BasicFileAttributes.class);
            return new MicroConfigFile(path, attrs.creationTime(), attrs.lastModifiedTime());
        } catch (IOException e) {
            throw new OSDFException("Can't get file attributes: " + path);
        }
    }

    public void delete() {
        try {
            Files.delete(path);
        } catch (IOException e) {
            throw new OSDFException("Can't delete file " + path, e);
        }
    }
}
