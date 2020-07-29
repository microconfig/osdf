package io.osdf.actions.system.install;

import io.osdf.settings.paths.OsdfPaths;
import lombok.RequiredArgsConstructor;

import java.nio.file.Path;
import java.util.List;

import static io.osdf.common.utils.CommandLineExecutor.execute;
import static io.osdf.common.utils.FileUtils.*;
import static java.lang.System.getProperty;
import static java.nio.file.Files.exists;
import static java.nio.file.Path.of;
import static org.apache.commons.io.FileUtils.getUserDirectoryPath;

@RequiredArgsConstructor
public class BashrcInstaller implements FileReplacer {
    private final OsdfPaths paths;
    private final Path tmpPath;
    private final Path dest;

    public static BashrcInstaller bashrcInstaller(OsdfPaths paths) {
        String shellPath = List.of(execute("echo $SHELL").split("\n")).get(0);
        String shellrc = shellPath.substring(shellPath.lastIndexOf("/") + 1) + "rc";
        if (shellrc.contains("bash") && getProperty("os.name").contains("Mac")) {
            return new BashrcInstaller(paths, of(paths.tmp() + "/bash_profile"),
                    of(getUserDirectoryPath() + "/.bash_profile"));
        }
        return new BashrcInstaller(paths, of(paths.tmp() + "/" + shellrc),
                of(getUserDirectoryPath() + "/." + shellrc));
    }

    @Override
    public void prepare() {
        writeStringToFile(tmpPath, content());
    }

    private String content() {
        String newEntry = withMarkers(newEntry());
        if (!exists(dest)) {
            return newEntry;
        }
        String shellrcContent = readAll(dest);
        if (shellrcContent.contains(newEntry)) {
            return shellrcContent;
        }
        return withoutOldEntry(shellrcContent) + "\n" + newEntry;
    }

    private String newEntry() {
        String path = "PATH=$PATH:" + paths.bin() + "/";
        String autocomplete = autocomplete();
        return path + "\n" + autocomplete;
    }

    private String autocomplete() {
        String source = "source ~/.osdf_completion";
        if (dest.getFileName().toString().contains("zsh")) {
            return "autoload compinit && compinit -u\n" +
                    "autoload bashcompinit && bashcompinit\n" +
                    source;
        }
        return source;
    }

    private String withoutOldEntry(String s) {
        int start = s.indexOf("# osdf-start");
        if (start == -1) return s;

        int end = s.indexOf("\n", s.indexOf("# osdf-end"));
        return s.substring(0, start) + s.substring(end + 1);
    }

    private String withMarkers(String s) {
        return "# osdf-start\n" + s + "\n# osdf-end\n";
    }

    @Override
    public void replace() {
        move(tmpPath, dest);
    }
}
