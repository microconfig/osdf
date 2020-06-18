package io.cluster.old.cluster.context;

import io.cluster.old.cluster.cli.ClusterCli;
import io.osdf.settings.paths.OsdfPaths;
import lombok.RequiredArgsConstructor;

import static io.cluster.old.cluster.cli.BaseClusterCli.baseClusterCLI;
import static io.cluster.old.cluster.kubernetes.KubernetesCli.kubernetes;
import static io.cluster.old.cluster.openshift.OpenShiftCli.oc;
import static io.microconfig.osdf.settings.SettingsFile.settingsFile;
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
