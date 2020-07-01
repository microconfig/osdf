package io.osdf.actions.system.install;

import io.osdf.settings.paths.OsdfPaths;
import lombok.RequiredArgsConstructor;

import java.nio.file.Path;

import static io.osdf.common.utils.CommandLineExecutor.execute;
import static io.osdf.common.utils.CommandLineExecutor.executeAndReadLines;
import static io.osdf.common.utils.FileUtils.readAll;
import static io.osdf.common.utils.FileUtils.writeStringToFile;
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
        String shellPath = executeAndReadLines("echo $SHELL").get(0);
        String shellrc = shellPath.substring(shellPath.lastIndexOf("/") + 1) + "rc";
        if (shellrc.contains("bash") && getProperty("os.name").contains("Mac")){
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
        String newEntry = "PATH=$PATH:" + paths.bin() + "/";
        if (!exists(dest)) {
            return newEntry;
        }
        String shellrcContent = readAll(dest);
        if (shellrcContent.contains(newEntry)) {
            return shellrcContent;
        }
        return shellrcContent + "\n" + newEntry + "\n";
    }

    @Override
    public void replace() {
        execute("cp " + tmpPath + " " + dest);
    }
}
