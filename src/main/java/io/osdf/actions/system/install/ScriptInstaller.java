package io.osdf.actions.system.install;

import io.osdf.settings.paths.OsdfPaths;
import lombok.RequiredArgsConstructor;

import java.nio.file.Path;

import static io.osdf.common.utils.CommandLineExecutor.execute;
import static io.osdf.common.utils.FileUtils.writeStringToFile;
import static io.osdf.common.utils.JarUtils.pathToJava;
import static java.nio.file.Path.of;

@RequiredArgsConstructor
public class ScriptInstaller implements FileReplacer {
    private final OsdfPaths paths;
    private final Path tmpPath;

    public static ScriptInstaller scriptInstaller(OsdfPaths paths) {
        return new ScriptInstaller(paths, of(paths.tmp() + "/osdf"));
    }

    @Override
    public void prepare() {
        String content = content();
        writeStringToFile(tmpPath, content);
        execute("chmod +x " + tmpPath);
    }

    @Override
    public void replace() {
        execute("cp " + tmpPath + " " + paths.bin() + "/osdf");
    }

    private String content() {
        return "if [ $# -gt 0  ] && [ $1 == \"logs\" ]\n" +
                "then\n" +
                "        trap '' SIGINT\n" +
                "fi\n" +
                pathToJava() + " -XX:TieredStopAtLevel=1 -jar " + paths.root() + "/osdf.jar ${@:1}";
    }
}