package io.osdf.actions.management.deploy.smart.hash;

import io.osdf.settings.paths.OsdfPaths;
import io.osdf.core.service.local.ServiceFiles;
import lombok.RequiredArgsConstructor;

import java.nio.file.Path;

import static io.osdf.actions.management.deploy.smart.hash.TotalHashComputer.totalHashComputer;
import static io.osdf.common.utils.FileUtils.readAll;
import static io.osdf.common.utils.FileUtils.writeStringToFile;
import static io.osdf.common.utils.YamlUtils.getString;
import static io.osdf.common.utils.YamlUtils.loadFromPath;

@RequiredArgsConstructor
public class ResourceHash {
    private final OsdfPaths paths;
    private final String resourcePath;

    public static ResourceHash deploymentHash(OsdfPaths paths) {
        return new ResourceHash(paths,"resources/deployment.yaml");
    }

    public static ResourceHash jobHash(OsdfPaths paths) {
        return new ResourceHash(paths, "resources/job.yaml");
    }

    public void insert(ServiceFiles files) {
        String currentHash = currentHash(files);
        if (currentHash != null) return;

        computeHash(files);
    }

    public String currentHash(ServiceFiles files) {
        String hash = getString(loadFromPath(deploymentPath(files)), "metadata", "labels", "configHash");
        return hash.equals("<CONFIG_HASH>") ? null : hash;
    }

    private String computeHash(ServiceFiles files) {
        String hash = totalHashComputer(paths, files).compute();
        Path deploymentPath = deploymentPath(files);
        String content = readAll(deploymentPath)
                .replace("<CONFIG_HASH>", hash);
        writeStringToFile(deploymentPath, content);
        return hash;
    }

    private Path deploymentPath(ServiceFiles files) {
        return files.getPath(resourcePath);
    }
}
