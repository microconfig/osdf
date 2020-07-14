package io.osdf.test;

import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.nio.file.Path;

import static io.osdf.common.utils.FileUtils.readAll;
import static java.nio.file.Path.of;

public class ClasspathReader {
    public static Path classpathFile(String name) {
        try {
            return of(new ClassPathResource(name).getFile().getAbsolutePath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String read(String file) {
        return readAll(classpathFile(file));
    }
}