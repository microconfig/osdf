package io.osdf.actions.system.install.migrations;

import io.osdf.common.SettingsFile;
import io.osdf.core.connection.context.ClusterContextSettings;
import io.osdf.settings.paths.OsdfPaths;

import static io.osdf.common.SettingsFile.settingsFile;
import static io.osdf.core.connection.context.ClusterType.OPENSHIFT;
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
