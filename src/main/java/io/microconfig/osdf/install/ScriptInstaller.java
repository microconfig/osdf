package io.microconfig.osdf.install;

import io.microconfig.osdf.paths.OSDFPaths;
import lombok.RequiredArgsConstructor;

import java.nio.file.Path;

import static io.microconfig.osdf.utils.CommandLineExecutor.execute;
import static io.microconfig.osdf.utils.FileUtils.writeStringToFile;
import static java.lang.System.getProperty;
import static java.nio.file.Path.of;
import static java.nio.file.Paths.get;

@RequiredArgsConstructor
public class ScriptInstaller implements FileReplacer {
    private final OSDFPaths paths;
    private final Path tmpPath;

    public static ScriptInstaller scriptInstaller(OSDFPaths paths) {
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
        Path pathToJava = get(getProperty("java.home").replace(" ", "\\ "), "bin", "java");
        return "if [ $# -gt 0  ] && [ $1 == \"logs\" ]\n" +
                "then\n" +
                "        trap '' SIGINT\n" +
                "fi\n" +
                pathToJava + " -XX:TieredStopAtLevel=1 -jar " + paths.root() + "/osdf.jar ${@:1}";
    }
}
