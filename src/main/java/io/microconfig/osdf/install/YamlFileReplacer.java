package io.microconfig.osdf.install;

import io.microconfig.osdf.paths.OSDFPaths;
import lombok.RequiredArgsConstructor;

import java.nio.file.Path;

import static io.microconfig.osdf.utils.CommandLineExecutor.execute;
import static io.microconfig.osdf.utils.YamlUtils.dump;
import static java.nio.file.Path.of;

@RequiredArgsConstructor
public class YamlFileReplacer implements FileReplacer {
    private final Object object;
    private final Path tmpPath;
    private final Path dest;


    public static YamlFileReplacer yamlFileReplacer(Object object, String name, OSDFPaths paths, Path dest) {
        return new YamlFileReplacer(object, of(paths.tmp() + "/" + name + ".yaml"), dest);
    }

    @Override
    public void prepare() {
        dump(object, tmpPath);
    }

    @Override
    public void replace() {
        execute("cp " + tmpPath + " " + dest);
    }
}
