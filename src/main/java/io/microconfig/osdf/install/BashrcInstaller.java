package io.microconfig.osdf.install;

import io.microconfig.osdf.paths.OSDFPaths;
import lombok.RequiredArgsConstructor;

import java.nio.file.Path;

import static io.microconfig.osdf.utils.CommandLineExecutor.execute;
import static io.microconfig.osdf.utils.CommandLineExecutor.executeAndReadLines;
import static io.microconfig.osdf.utils.FileUtils.readAll;
import static io.microconfig.osdf.utils.FileUtils.writeStringToFile;
import static java.lang.System.getProperty;
import static java.nio.file.Files.exists;
import static java.nio.file.Path.of;
import static org.apache.commons.io.FileUtils.getUserDirectoryPath;

@RequiredArgsConstructor
public class BashrcInstaller implements FileReplacer {
    private final OSDFPaths paths;
    private final Path tmpPath;
    private final Path dest;

    public static BashrcInstaller bashrcInstaller(OSDFPaths paths) {
        String shellPath = executeAndReadLines("echo $SHELL").get(0);
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
        String newEntry = "PATH=$PATH:" + paths.bin() + "/" + "\n";
        String additionalEntry = "\n" + "source .osdf_completion" + "\n";
        if (dest.getFileName().toString().contains("zsh")) {
            additionalEntry = "autoload compinit && compinit -u"
                    + "\n" + "autoload bashcompinit && bashcompinit" + additionalEntry;
        }
        if (!exists(dest)) {
            return newEntry + additionalEntry;
        }
        String shellrcContent = readAll(dest);
        if (shellrcContent.contains(newEntry + additionalEntry)) {
            return shellrcContent;
        }
        return shellrcContent + "\n" + newEntry + "\n" + additionalEntry;
    }

    @Override
    public void replace() {
        execute("cp " + tmpPath + " " + dest);
    }
}
