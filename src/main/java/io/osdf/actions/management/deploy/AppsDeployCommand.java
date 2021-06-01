package io.osdf.actions.management.deploy;

import io.osdf.actions.management.deploy.smart.hash.ResourcesHashComputer;
import io.osdf.actions.management.deploy.smart.image.ImageVersionReplacer;
import io.osdf.core.application.core.Application;
import io.osdf.core.connection.cli.ClusterCli;
import io.osdf.core.events.EventLevel;
import io.osdf.core.events.EventSender;
import io.osdf.settings.paths.OsdfPaths;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static io.osdf.actions.management.deploy.DeployTypeAppsFilter.deployTypeAppsFilter;
import static io.osdf.actions.management.deploy.GroupDeployer.groupDeployer;
import static io.osdf.actions.management.deploy.groups.StartGroupSplitter.startGroupSplitter;
import static io.osdf.actions.management.deploy.smart.UpToDateAppFilter.upToDateAppFilter;
import static io.osdf.actions.management.deploy.smart.hash.ResourcesHashComputer.resourcesHashComputer;
import static io.osdf.actions.management.deploy.smart.image.ImageVersionReplacer.imageVersionReplacer;
import static io.osdf.actions.management.deploy.smart.image.digest.DigestGetterImpl.digestGetter;
import static io.osdf.core.application.core.AllApplications.all;
import static io.osdf.core.application.core.files.loaders.ApplicationFilesLoaderImpl.activeRequiredAppsLoader;
import static io.osdf.core.events.EventLevel.INFO;
import static io.osdf.settings.OsdfConfig.osdfConfig;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.joining;

@RequiredArgsConstructor
public class AppsDeployCommand {
    private final OsdfPaths paths;
    private final ClusterCli cli;
    private final EventSender events;
    private final EventLevel level;

    public static AppsDeployCommand deployCommand(OsdfPaths paths, ClusterCli cli, EventSender eventSender, EventLevel level) {
        return new AppsDeployCommand(paths, cli, eventSender, level);
    }

    public boolean deploy(List<String> requiredServiceNames, boolean smart, String type) {
        List<Application> allApps = activeRequiredAppsLoader(paths, requiredServiceNames).load(all(cli));
        List<Application> appsForType = deployTypeAppsFilter(paths).filter(allApps, type);
        if (appsForType.isEmpty()) return true;

        preprocessServices(appsForType);

        List<Application> appsToDeploy = filterApps(smart, appsForType);
        if (appsToDeploy.isEmpty()) return true;

        GroupDeployer groupDeployer = groupDeployer(cli, osdfConfig(paths).maxParallel(), events, level);
        List<List<Application>> groups = startGroupSplitter().split(appsToDeploy);
        return groups.stream().allMatch(groupDeployer::deployGroup);
    }

    private List<Application> filterApps(boolean smart, List<Application> allApps) {
        List<Application> appsToDeploy = smart ? upToDateAppFilter(cli).filter(allApps) : allApps;
        if (appsToDeploy.isEmpty()) {
            events.send("All apps are up-to-date", INFO);
            return emptyList();
        }
        events.send("Deploying: " +
                appsToDeploy.stream()
                        .map(Application::name)
                        .collect(joining(" ")), level);
        return appsToDeploy;
    }

    private void preprocessServices(List<Application> apps) {
        ResourcesHashComputer resourcesHashComputer = resourcesHashComputer();
        ImageVersionReplacer tagReplacer = imageVersionReplacer(digestGetter(paths));

        apps.forEach(service -> tagReplacer.replaceFor(service.files()));
        apps.forEach(service -> resourcesHashComputer.insertIn(service.files()));
    }
}
