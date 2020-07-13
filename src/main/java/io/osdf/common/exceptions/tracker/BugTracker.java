package io.osdf.common.exceptions.tracker;

import lombok.RequiredArgsConstructor;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Path;

import static io.osdf.common.utils.FileUtils.appendToFile;
import static java.nio.file.Path.of;

@RequiredArgsConstructor
public class BugTracker {
    private final Path osdfDir;

    public static BugTracker bugTracker(Path osdfDir) {
        return new BugTracker(osdfDir);
    }

    public void save(String command, Exception e) {
        appendToFile(path(), bugMessage(command, e));
    }

    private String bugMessage(String command, Exception e) {
        return "=====\n" +
                command + "\n" +
                exceptionMessage(e) + "\n" +
                "=====\n";
    }

    private String exceptionMessage(Exception e) {
        StringWriter writer = new StringWriter();
        e.printStackTrace(new PrintWriter(writer));
        return writer.toString();
    }

    private Path path() {
        return of(osdfDir + "/bugs.txt");
    }

}
