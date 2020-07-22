package io.osdf.actions.system.install;

import io.osdf.settings.paths.OsdfPaths;
import lombok.RequiredArgsConstructor;
import org.yaml.snakeyaml.Yaml;

import java.nio.file.Path;

import static io.osdf.common.encryption.Encryption.encryptor;
import static io.osdf.common.utils.CommandLineExecutor.execute;
import static io.osdf.common.utils.FileUtils.writeStringToFile;
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
        String content = new Yaml().dump(object);
        writeStringToFile(tmpPath, encryptor.encrypt(content));
    }

    @Override
    public void replace() {
        execute("mv " + tmpPath + " " + dest);
    }
}
