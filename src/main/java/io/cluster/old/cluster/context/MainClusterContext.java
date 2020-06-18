package io.cluster.old.cluster.context;

import io.cluster.old.cluster.cli.ClusterCLI;
import io.osdf.settings.paths.OSDFPaths;
import lombok.RequiredArgsConstructor;

import static io.cluster.old.cluster.cli.BaseClusterCLI.baseClusterCLI;
import static io.cluster.old.cluster.kubernetes.KubernetesCLI.kubernetes;
import static io.cluster.old.cluster.openshift.OpenShiftCLI.oc;
import static io.microconfig.osdf.settings.SettingsFile.settingsFile;
import static java.nio.file.Files.exists;

@RequiredArgsConstructor
public class MainClusterContext implements ClusterContext {
    private final OSDFPaths paths;

    public static MainClusterContext mainClusterContext(OSDFPaths paths) {
        return new MainClusterContext(paths);
    }

    @Override
    public ClusterCLI cli() {
        if (!exists(paths.settings().clusterContext())) return baseClusterCLI();
        ClusterType type = settingsFile(ClusterContextSettings.class, paths.settings().clusterContext()).getSettings().getType();
        switch(type) {
            case OPENSHIFT: return oc(paths);
            case KUBERNETES: return kubernetes();
            default: return baseClusterCLI();
        }
    }
}
