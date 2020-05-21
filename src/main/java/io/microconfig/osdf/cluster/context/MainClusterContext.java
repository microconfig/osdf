package io.microconfig.osdf.cluster.context;

import io.microconfig.osdf.cluster.cli.ClusterCLI;
import io.microconfig.osdf.paths.OSDFPaths;
import lombok.RequiredArgsConstructor;

import static io.microconfig.osdf.cluster.cli.BaseClusterCLI.baseClusterCLI;
import static io.microconfig.osdf.cluster.kubernetes.KubernetesCLI.kubernetes;
import static io.microconfig.osdf.openshift.OpenShiftCLI.oc;
import static io.microconfig.osdf.settings.SettingsFile.settingsFile;

@RequiredArgsConstructor
public class MainClusterContext implements ClusterContext {
    private final OSDFPaths paths;

    public static MainClusterContext mainClusterContext(OSDFPaths paths) {
        return new MainClusterContext(paths);
    }

    @Override
    public ClusterCLI cli() {
        ClusterType type = settingsFile(ClusterContextSettings.class, paths.settings().clusterContext()).getSettings().getType();
        switch(type) {
            case OPENSHIFT: return oc(paths);
            case KUBERNETES: return kubernetes();
            default: return baseClusterCLI();
        }
    }
}
