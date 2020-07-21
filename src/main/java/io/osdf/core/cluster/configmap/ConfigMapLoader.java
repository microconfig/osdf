package io.osdf.core.cluster.configmap;

import io.osdf.common.exceptions.OSDFException;
import io.osdf.core.connection.cli.CliOutput;
import io.osdf.core.connection.cli.ClusterCli;
import lombok.RequiredArgsConstructor;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static io.osdf.common.utils.FileUtils.createTempDirectory;
import static io.osdf.common.utils.YamlUtils.createFromString;
import static io.osdf.common.utils.YamlUtils.dump;
import static java.nio.file.Path.of;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toUnmodifiableList;

@RequiredArgsConstructor
public class ConfigMapLoader {
    private final ClusterCli cli;

    public static ConfigMapLoader configMapLoader(ClusterCli cli) {
        return new ConfigMapLoader(cli);
    }

    public void upload(String name, Map<String, Object> dataObjects) {
        List<Path> files = createFiles(name, dataObjects);
        deleteConfigMapIfExists(name);
        createConfigMap(name, files);
    }

    public <T> T load(String name, String key, Class<T> fileClass) {
        CliOutput output = cli.execute("get configmap " + name + " -o custom-columns=\"config:.data." + key + "\"");
        if (!output.ok()) {
            if (output.getOutput().contains("not found")) throw new OSDFException("Configmap " + name + " doesn't exist");
            output.throwExceptionIfError();
        }

        String content = output
                .getOutput()
                .replaceFirst("config\n", "");
        return createFromString(fileClass, content);
    }

    private List<Path> createFiles(String name, Map<String, Object> dataObjects) {
        Path tmpDir = createTempDirectory(name);
        dataObjects.forEach((filename, dataObject) -> dump(dataObject, of(tmpDir + "/" + filename)));
        return dataObjects.keySet().stream()
                .map(filename -> of(tmpDir + "/" + filename))
                .collect(toUnmodifiableList());
    }

    private void deleteConfigMapIfExists(String name) {
        CliOutput output = cli.execute("delete configmap " + name);
        if (!output.ok() && !output.getOutput().contains("not found"))
            throw new OSDFException("Error deleting config map: " + output.getOutput());
    }

    private void createConfigMap(String name, List<Path> files) {
        String filesOptions = files.stream()
                .map(path -> "--from-file=" + path)
                .collect(joining(" "));
        cli.execute("create configmap " + name + " " + filesOptions)
                .throwExceptionIfError();
    }
}
