package io.microconfig.osdf.api.implementations;

import io.microconfig.osdf.api.declarations.ManagementApi;
import io.microconfig.osdf.cluster.cli.ClusterCLI;
import io.microconfig.osdf.commands.DeletePodCommand;
import io.microconfig.osdf.commands.DeployCommand;
import io.microconfig.osdf.commands.RestartCommand;
import io.microconfig.osdf.commands.StopCommand;
import io.microconfig.osdf.components.DeploymentComponent;
import io.microconfig.osdf.deployers.Deployer;
import io.microconfig.osdf.exceptions.OSDFException;
import io.microconfig.osdf.paths.OSDFPaths;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static io.microconfig.osdf.components.loader.ComponentsLoaderImpl.componentsLoader;
import static io.microconfig.osdf.deployers.CanaryDeployer.canaryDeployer;
import static io.microconfig.osdf.deployers.HiddenDeployer.hiddenDeployer;
import static io.microconfig.osdf.deployers.ReplaceDeployer.replaceDeployer;
import static io.microconfig.osdf.deployers.RestrictedDeployer.restrictedDeployer;
import static io.microconfig.osdf.metrics.formats.PrometheusParser.prometheusParser;
import static io.microconfig.osdf.openshift.OpenShiftCLI.oc;

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
        new RestartCommand(paths, oc(cli)).run(components);
    }

    @Override
    public void stop(List<String> components) {
        cli.login();
        new StopCommand(paths, oc(cli)).run(components);
    }

    @Override
    public void deletePod(String component, List<String> pods) {
        cli.login();
        new DeletePodCommand(paths, oc(cli)).delete(component, pods);
    }

    @Override
    public void clearDeployments(String version) {
        cli.login();
        componentsLoader(paths, null, oc(cli))
                .load(DeploymentComponent.class)
                .forEach(component -> component.deleteDeploymentConfig(version));
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
