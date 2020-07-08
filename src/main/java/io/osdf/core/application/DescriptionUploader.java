package io.osdf.core.application;

import io.osdf.common.exceptions.OSDFException;
import io.osdf.core.connection.cli.CliOutput;
import io.osdf.core.connection.cli.ClusterCli;
import lombok.RequiredArgsConstructor;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static io.osdf.common.utils.FileUtils.createDirectoriesIfNotExists;
import static io.osdf.common.utils.YamlUtils.dump;
import static java.nio.file.Path.of;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toUnmodifiableList;

@RequiredArgsConstructor
public class DescriptionUploader {
    private final ClusterCli cli;

    public static DescriptionUploader descriptionUploader(ClusterCli cli) {
        return new DescriptionUploader(cli);
    }

    public void upload(String name, Map<String, Object> descriptions) {
        Path tmpDir = of("/tmp/osdf/" + name);
        createDirectoriesIfNotExists(tmpDir);
        descriptions.forEach((filename, description) -> dump(description, of(tmpDir + "/" + filename)));
        List<Path> files = descriptions.keySet().stream()
                .map(filename -> of(tmpDir + "/" + filename))
                .collect(toUnmodifiableList());

        deleteConfigMapIfExists(name);
        createConfigMap(name, files);
    }

    private void createConfigMap(String name, List<Path> files) {
        String filesOptions = files.stream()
                .map(path -> "--from-file=" + path)
                .collect(joining(" "));
        cli.execute("create configmap " + name + " " + filesOptions)
                .throwExceptionIfError();
    }

    private void deleteConfigMapIfExists(String name) {
        CliOutput output = cli.execute("delete configmap " + name);
        if (!output.ok() && !output.getOutput().contains("not found"))
            throw new OSDFException("Error deleting config map: " + output.getOutput());
    }
}
