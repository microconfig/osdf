package io.osdf.actions.system.install;

import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;

import static java.nio.file.Files.readAttributes;
import static org.junit.jupiter.api.Assertions.assertTrue;

@RequiredArgsConstructor
public class FileReplacerTester {
    private final FileReplacer fileReplacer;
    private final Path path;

    public static FileReplacerTester fileReplacerTester(FileReplacer fileReplacer, Path path) {
        return new FileReplacerTester(fileReplacer, path);
    }

    public void replaceAndCheck() throws IOException {
        long oldJarModifiedTime = modifiedTime(path);
        fileReplacer.prepare();
        fileReplacer.replace();
        long newJarModifiedTime = modifiedTime(path);
        assertTrue(newJarModifiedTime > oldJarModifiedTime);

    }

    private long modifiedTime(Path path) throws IOException {
        BasicFileAttributes attributes = readAttributes(path, BasicFileAttributes.class);
        return attributes.lastModifiedTime().toMillis();
    }
}
