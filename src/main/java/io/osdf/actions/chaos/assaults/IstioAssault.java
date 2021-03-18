package io.osdf.actions.chaos.assaults;

import io.osdf.actions.management.deploy.deployer.plain.PlainAppDeployer;
import io.osdf.core.application.plain.PlainApplication;
import io.osdf.core.connection.cli.ClusterCli;
import io.osdf.settings.paths.OsdfPaths;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static io.microconfig.utils.Logger.info;
import static io.osdf.actions.management.deploy.deployer.plain.PlainAppDeployer.plainAppDeployer;
import static io.osdf.core.application.core.files.loaders.ApplicationFilesLoaderImpl.activeRequiredAppsLoader;
import static io.osdf.core.application.plain.PlainApplicationMapper.plain;

@RequiredArgsConstructor
public class IstioAssault implements Assault {
    private final PlainAppDeployer deployer;
    private final List<PlainApplication> istioApps;

    @SuppressWarnings("unchecked")
    public static IstioAssault istioAssault(Object description, ClusterCli cli, OsdfPaths paths) {
        List<String> components = (List<String>) description;
        List<PlainApplication> apps = activeRequiredAppsLoader(paths, components).load(plain(cli));
        return new IstioAssault(plainAppDeployer(cli), apps);
    }

    @Override
    public void start() {
        istioApps.forEach(istioApp -> {
            deployer.deploy(istioApp);
            info(istioApp.name() + " deployed");
        });
    }

    @Override
    public void stop() {
        istioApps.forEach(istioApp -> {
            istioApp.delete();
            info(istioApp.name() + " deleted");
        });
    }
}
