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
        String bashFile = getProperty("os.name").contains("Mac") ? "/.bash_profile" : "/.bashrc";
        return new BashrcInstaller(paths, of(paths.tmp() + "/bashrc"), of(getUserDirectoryPath() + bashFile));
    }

    @Override
    public void prepare() {
        writeStringToFile(tmpPath, content());
        synchroniseShellSource();
    }

    private String content() {
        String newEntry = "PATH=$PATH:" + paths.bin() + "/";
        if (!exists(dest)) {
            return newEntry;
        }
        String bashrcContent = readAll(dest);
        if (bashrcContent.contains(newEntry)) {
            return bashrcContent;
        }
        return bashrcContent + "\n" + newEntry + "\n";
    }

    public void synchroniseShellSource() {
        if (executeAndReadLines("echo $SHELL").get(0).contains("zsh")) {
            String zshrcContent = readAll(of(getUserDirectoryPath() + "/.zshrc"));
            String newEntry = "PATH=$PATH:" + paths.bin() + "/";
            if (!zshrcContent.contains(newEntry))
                zshrcContent += "\n" + newEntry + "\n";
            writeStringToFile(of(getUserDirectoryPath() + "/.zshrc"), zshrcContent);
        }
    }

    @Override
    public void replace() {
        execute("cp " + tmpPath + " " + dest);
    }
}
