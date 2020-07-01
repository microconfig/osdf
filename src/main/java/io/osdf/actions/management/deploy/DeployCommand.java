package io.osdf.actions.management.deploy;

import io.osdf.core.connection.cli.ClusterCli;
import io.osdf.actions.management.deploy.deployers.ServiceDeployer;
import io.osdf.common.exceptions.OSDFException;
import io.osdf.settings.paths.OsdfPaths;
import io.osdf.actions.management.deploy.smart.hash.ResourceHash;
import io.osdf.core.service.core.deployment.pack.ServiceDeployPack;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static io.osdf.actions.management.deploy.deployers.BaseServiceDeployer.baseServiceDeployer;
import static io.osdf.actions.management.deploy.deployers.RestrictedDeployer.restrictedDeployer;
import static io.osdf.actions.management.deploy.smart.hash.ResourceHash.deploymentHash;
import static io.osdf.core.service.core.deployment.pack.loader.DefaultServiceDeployPacksLoader.serviceLoader;
import static io.osdf.actions.management.deploy.smart.UpToDateDeploymentFilter.upToDateDeploymentFilter;
import static io.osdf.core.service.local.loaders.filters.RequiredComponentsFilter.requiredComponentsFilter;
import static io.microconfig.utils.Logger.announce;
import static java.util.stream.Collectors.joining;
import static java.util.stream.IntStream.range;

@RequiredArgsConstructor
public class DeployCommand {
    private final OsdfPaths paths;
    private final ClusterCli cli;

    public static DeployCommand deployCommand(OsdfPaths paths, ClusterCli cli) {
        return new DeployCommand(paths, cli);
    }

    public void deploy(List<String> serviceNames, String mode, Boolean smart) {
        ServiceDeployer deployer = getDeployer(mode);

        List<ServiceDeployPack> deployPacks = getDeployPacks(serviceNames, smart);
        if (deployPacks.isEmpty()) return;

        callDeployer(deployPacks, deployer);
    }

    private List<ServiceDeployPack> getDeployPacks(List<String> serviceNames, boolean smart) {
        List<ServiceDeployPack> allPacks = serviceLoader(paths, requiredComponentsFilter(serviceNames), cli).loadPacks();

        ResourceHash resourceHash = deploymentHash(paths);
        allPacks.forEach(pack -> resourceHash.insert(pack.files()));

        List<ServiceDeployPack> deployPacks = smart ? upToDateDeploymentFilter(cli).filter(allPacks, resourceHash) : allPacks;
        if (deployPacks.isEmpty())  {
            announce("All services are up-to-date");
        } else {
            announce("Deploying: " +
                    deployPacks.stream()
                            .map(deployPack -> deployPack.service().name())
                            .collect(joining(" ")));
        }
        return deployPacks;
    }

    private void callDeployer(List<ServiceDeployPack> deployPacks, ServiceDeployer deployer) {
        range(0, deployPacks.size()).forEach(i -> deployer.deploy(
                deployPacks.get(i).service(),
                deployPacks.get(i).deployment(),
                deployPacks.get(i).files())
        );
    }

    private ServiceDeployer getDeployer(String mode) {
        if (mode == null) {
            return baseServiceDeployer(cli);
        }
        if (mode.equals("restricted")) {
            return restrictedDeployer();
        }
        throw new OSDFException("Unknown deploy mode");
    }
}
