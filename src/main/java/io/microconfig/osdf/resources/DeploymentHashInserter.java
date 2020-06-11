package io.microconfig.osdf.resources;

import io.microconfig.osdf.service.files.ServiceFiles;

import java.nio.file.Path;

import static io.microconfig.osdf.cluster.resource.totalhash.TotalHashComputer.totalHashComputer;
import static io.microconfig.osdf.utils.FileUtils.readAll;
import static io.microconfig.osdf.utils.FileUtils.writeStringToFile;
import static io.microconfig.osdf.utils.YamlUtils.getString;
import static io.microconfig.osdf.utils.YamlUtils.loadFromPath;

public class DeploymentHashInserter {
    public static DeploymentHashInserter deploymentHashInserter() {
        return new DeploymentHashInserter();
    }

    public String insert(ServiceFiles files) {
        String computedHash = computedHash(files);
        if (computedHash != null) return computedHash;

        return computeHash(files);
    }

    private String computeHash(ServiceFiles files) {
        String hash = totalHashComputer(files).compute();
        Path deploymentPath = deploymentPath(files);
        String content = readAll(deploymentPath)
                .replace("<CONFIG_HASH>", hash);
        writeStringToFile(deploymentPath, content);
        return hash;
    }

    private String computedHash(ServiceFiles files) {
        String hash = getString(loadFromPath(deploymentPath(files)), "metadata", "labels", "configHash");
        if (!hash.equals("<CONFIG_HASH>")) return hash;
        return null;
    }

    private Path deploymentPath(ServiceFiles files) {
        return files.getPath("resources/deployment.yaml");
    }
}
