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
    private final Path destination;

    public static FileReplacerTester fileReplacerTester(FileReplacer fileReplacer, Path destination) {
        return new FileReplacerTester(fileReplacer, destination);
    }

    public void replaceAndCheck() throws IOException {
        long oldJarModifiedTime = modifiedTime(destination);
        fileReplacer.prepare();
        fileReplacer.replace();
        long newJarModifiedTime = modifiedTime(destination);
        assertTrue(newJarModifiedTime > oldJarModifiedTime);
    }

    private long modifiedTime(Path path) throws IOException {
        BasicFileAttributes attributes = readAttributes(path, BasicFileAttributes.class);
        return attributes.lastModifiedTime().toMillis();
    }
}
