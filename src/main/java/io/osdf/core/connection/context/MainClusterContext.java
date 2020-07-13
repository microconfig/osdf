package io.osdf.core.connection.context;

import io.osdf.core.connection.cli.ClusterCli;
import io.osdf.settings.paths.OsdfPaths;
import lombok.RequiredArgsConstructor;

import static io.osdf.core.connection.cli.BaseClusterCli.baseClusterCLI;
import static io.osdf.core.connection.cli.kubernetes.KubernetesCli.kubernetes;
import static io.osdf.core.connection.cli.openshift.OpenShiftCli.oc;
import static io.osdf.common.SettingsFile.settingsFile;
import static java.nio.file.Files.exists;

@RequiredArgsConstructor
public class MainClusterContext implements ClusterContext {
    private final OsdfPaths paths;

    public static MainClusterContext mainClusterContext(OsdfPaths paths) {
        return new MainClusterContext(paths);
    }

    @Override
    public ClusterCli cli() {
        if (!exists(paths.settings().clusterContext())) return baseClusterCLI();
        ClusterType type = settingsFile(ClusterContextSettings.class, paths.settings().clusterContext()).getSettings().getType();
        switch(type) {
            case OPENSHIFT: return oc(paths);
            case KUBERNETES: return kubernetes();
            default: return baseClusterCLI();
        }
    }
}
