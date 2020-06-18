package io.microconfig.osdf.install.migrations;

import io.cluster.old.cluster.context.ClusterContextSettings;
import io.osdf.settings.paths.OsdfPaths;
import io.microconfig.osdf.settings.SettingsFile;

import static io.cluster.old.cluster.context.ClusterType.OPENSHIFT;
import static io.microconfig.osdf.settings.SettingsFile.settingsFile;
import static java.nio.file.Files.exists;

public class CreateClusterContext implements Migration {
    public static CreateClusterContext createClusterContext() {
        return new CreateClusterContext();
    }

    @Override
    public void apply(OsdfPaths paths) {
        if (exists(paths.settings().clusterContext())) return;
        if (!exists(paths.settings().openshift())) return;
        SettingsFile<ClusterContextSettings> settingsFile = settingsFile(ClusterContextSettings.class, paths.settings().clusterContext());
        settingsFile.getSettings().setType(OPENSHIFT);
        settingsFile.save();
    }
}
