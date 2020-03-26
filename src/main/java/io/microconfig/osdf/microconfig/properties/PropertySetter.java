package io.microconfig.osdf.microconfig.properties;

import lombok.NoArgsConstructor;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Scanner;

import static java.nio.file.Files.newInputStream;
import static java.nio.file.Files.writeString;

@NoArgsConstructor
public class PropertySetter {
    public static PropertySetter propertySetter() {
        return new PropertySetter();
    }

    public void setIfNecessary(Path file, String key, String newValue) {
        if (newValue == null) return;
        String result = readAndModify(file, key, newValue);
        save(file, result);
    }

    private void save(Path path, String result) {
        try {
            writeString(path, result);
        } catch (IOException e) {
            throw new RuntimeException("Error writing project version file: " + path, e);
        }
    }

    private String readAndModify(Path file, String key, String newValue) {
        StringBuilder result = new StringBuilder();
        try (InputStream inputStream = newInputStream(file)) {
            Scanner scanner = new Scanner(inputStream);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().strip();
                if (line.startsWith(key + "=")) {
                    if (newValue.startsWith("-")) {
                        line += newValue;
                    } else {
                        line = key + "=" + newValue;
                    }
                }
                result.append(line).append("\n");
            }
        } catch (IOException e) {
            throw new RuntimeException("Error reading project version file: " + file, e);
        }
        return result.toString();
    }
}
