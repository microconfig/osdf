package io.osdf.actions.management.clearapps;

import io.osdf.core.application.core.AbstractApplication;
import io.osdf.core.application.core.Application;
import io.osdf.core.connection.cli.ClusterCli;
import io.osdf.settings.paths.OsdfPaths;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static io.osdf.actions.management.clearapps.AppList.appList;
import static io.osdf.actions.management.clearapps.AppList.remoteAppList;
import static io.osdf.core.application.core.AbstractApplication.remoteApplication;
import static io.osdf.core.application.core.AllApplications.all;
import static io.osdf.core.application.core.files.loaders.ApplicationFilesLoaderImpl.appLoader;
import static java.util.function.Predicate.not;
import static java.util.stream.Collectors.toUnmodifiableList;

@RequiredArgsConstructor
public class ClearAppsCommand {
    private final ClusterCli cli;
    private final OsdfPaths paths;

    public static ClearAppsCommand clearAppsCommand(ClusterCli cli, OsdfPaths paths) {
        return new ClearAppsCommand(cli, paths);
    }

    public void clear() {
        List<String> localApps = appLoader(paths).load(all(cli))
                .stream()
                .map(Application::name)
                .collect(toUnmodifiableList());

        remoteAppList(cli)
                .getApps().stream()
                .filter(not(localApps::contains))
                .map(name -> remoteApplication(name, cli))
                .forEach(AbstractApplication::delete);

        appList(localApps).upload(cli);
    }
}
