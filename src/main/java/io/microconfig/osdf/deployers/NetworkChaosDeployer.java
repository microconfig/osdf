package io.microconfig.osdf.deployers;

import io.microconfig.osdf.cluster.cli.ClusterCLI;
import io.microconfig.osdf.cluster.openshift.OpenShiftCLI;
import io.microconfig.osdf.deprecated.components.DeploymentComponent;
import io.microconfig.osdf.istio.VirtualService;
import io.microconfig.osdf.istio.faults.Fault;
import lombok.AllArgsConstructor;

import static io.microconfig.osdf.istio.VirtualService.virtualService;
import static io.microconfig.utils.Logger.announce;


@AllArgsConstructor
public class NetworkChaosDeployer {

    private final ClusterCLI cli;
    private final Fault fault;

    public static NetworkChaosDeployer chaosDeployer(ClusterCLI cli) {
        return new NetworkChaosDeployer(cli, null);
    }

    public static NetworkChaosDeployer chaosDeployer(ClusterCLI cli, Fault fault) {
        return new NetworkChaosDeployer(cli, fault);
    }


    public void deploy(DeploymentComponent component) {
        if (!component.isIstioService()) return;

        //fixme
        if (cli instanceof OpenShiftCLI) {
            VirtualService virtualService = virtualService((OpenShiftCLI) cli, component);
            virtualService.setFault(fault).upload();
            if (fault != null) {
                announce("Network fault injected into " + component);
            } else {
                announce("Network faults removed from " + component);
            }
        }

    }
}
