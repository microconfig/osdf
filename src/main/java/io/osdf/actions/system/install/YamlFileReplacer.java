package io.osdf.actions.system.install;

import io.osdf.settings.paths.OsdfPaths;
import lombok.RequiredArgsConstructor;

import java.nio.file.Path;

import static io.osdf.common.utils.CommandLineExecutor.execute;
import static io.osdf.common.utils.YamlUtils.dump;
import static java.nio.file.Path.of;

@RequiredArgsConstructor
public class YamlFileReplacer implements FileReplacer {
    private final Object object;
    private final Path tmpPath;
    private final Path dest;


    public static YamlFileReplacer yamlFileReplacer(Object object, String name, OsdfPaths paths, Path dest) {
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
