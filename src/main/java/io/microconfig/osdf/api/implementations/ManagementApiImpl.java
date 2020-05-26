package io.microconfig.osdf.api.implementations;

import io.microconfig.osdf.api.declarations.ManagementApi;
import io.microconfig.osdf.cluster.cli.ClusterCLI;
import io.microconfig.osdf.commands.DeployCommand;
import io.microconfig.osdf.deployers.Deployer;
import io.microconfig.osdf.develop.service.deployment.pack.ServiceDeployPack;
import io.microconfig.osdf.exceptions.OSDFException;
import io.microconfig.osdf.paths.OSDFPaths;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static io.microconfig.osdf.cluster.pod.PodDeleter.podDeleter;
import static io.microconfig.osdf.deployers.CanaryDeployer.canaryDeployer;
import static io.microconfig.osdf.deployers.HiddenDeployer.hiddenDeployer;
import static io.microconfig.osdf.deployers.ReplaceDeployer.replaceDeployer;
import static io.microconfig.osdf.deployers.RestrictedDeployer.restrictedDeployer;
import static io.microconfig.osdf.develop.service.deployment.tools.DeploymentRestarter.deploymentRestarter;
import static io.microconfig.osdf.develop.service.deployment.pack.loader.DefaultServiceDeployPacksLoader.defaultServiceDeployPacksLoader;
import static io.microconfig.osdf.metrics.formats.PrometheusParser.prometheusParser;
import static io.microconfig.osdf.openshift.OpenShiftCLI.oc;
import static io.microconfig.utils.Logger.info;

@RequiredArgsConstructor
public class ManagementApiImpl implements ManagementApi {
    private final OSDFPaths paths;
    private final ClusterCLI cli;

    public static ManagementApi managementApi(OSDFPaths paths, ClusterCLI cli) {
        return new ManagementApiImpl(paths, cli);
    }

    @Override
    public void deploy(List<String> components, String mode, Boolean wait) {
        cli.login();
        new DeployCommand(paths, oc(cli), deployer(mode), wait).run(components);
    }

    @Override
    public void restart(List<String> components) {
        cli.login();
        List<ServiceDeployPack> deployPacks = defaultServiceDeployPacksLoader(paths, components, cli).loadPacks();
        deployPacks.forEach(pack -> deploymentRestarter().restart(pack.deployment(), pack.files()));
    }

    @Override
    public void stop(List<String> components) {
        cli.login();
        List<ServiceDeployPack> deployPacks = defaultServiceDeployPacksLoader(paths, components, cli).loadPacks();
        deployPacks.forEach(pack -> pack.deployment().scale(0));
    }

    @Override
    public void deletePod(String component, List<String> pods) {
        cli.login();
        podDeleter(paths, cli).delete(component, pods);
    }

    @Override
    public void clearDeployments(String version) {
        cli.login();
        List<ServiceDeployPack> deployPacks = defaultServiceDeployPacksLoader(paths, cli).loadPacks();
        deployPacks.forEach(pack -> {
            String output = cli.execute("delete dc " + pack.service().name() + "." + version).getOutput();
            info(output);
        });
    }

    private Deployer deployer(String mode) {
        if (mode == null || mode.equals("replace")) {
            return replaceDeployer(oc(cli), paths);
        }
        if (mode.equals("hidden")) {
            return hiddenDeployer(oc(cli));
        }
        if (mode.equals("canary")) {
            return canaryDeployer(oc(cli), prometheusParser());
        }
        if (mode.equals("restricted")) {
            return restrictedDeployer(oc(cli));
        }
        throw new OSDFException("Unknown deploy mode");
    }
}
