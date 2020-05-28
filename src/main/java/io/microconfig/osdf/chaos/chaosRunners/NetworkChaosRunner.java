package io.microconfig.osdf.chaos.chaosRunners;

import io.microconfig.osdf.chaos.ChaosSet;
import io.microconfig.osdf.cluster.cli.ClusterCLI;
import io.microconfig.osdf.deployers.NetworkChaosDeployer;
import io.microconfig.osdf.istio.faults.Fault;
import io.microconfig.osdf.paths.OSDFPaths;
import io.microconfig.osdf.service.deployment.pack.ServiceDeployPack;
import lombok.AllArgsConstructor;

import java.util.List;

import static io.microconfig.osdf.deployers.NetworkChaosDeployer.chaosDeployer;
import static io.microconfig.osdf.istio.faults.Fault.fault;
import static io.microconfig.osdf.service.deployment.pack.loader.DefaultServiceDeployPacksLoader.defaultServiceDeployPacksLoader;
import static io.microconfig.osdf.utils.IstioUtils.isIstioService;


@AllArgsConstructor
public class NetworkChaosRunner implements ChaosRunner {
    private final OSDFPaths paths;
    private final ClusterCLI cli;

    public static ChaosRunner networkChaosRunner(OSDFPaths paths, ClusterCLI oc) {
        return new NetworkChaosRunner(paths, oc);
    }

    @Override
    public void run(List<String> components, ChaosSet chaosSet, Integer severity, Integer duration) {
        deploy(components, fault(chaosSet.getHttpErrorCode(), severity, chaosSet.getHttpDelay(), severity));
    }

    @Override
    public void stop(List<String> components) {
        deploy(components, null);
    }

    private void deploy(List<String> components, Fault fault) {
        NetworkChaosDeployer deployer = chaosDeployer(fault);
        List<ServiceDeployPack> deployPacks = defaultServiceDeployPacksLoader(paths, components, cli).loadPacks();
        deployPacks.stream()
                .filter(pack -> isIstioService(pack.service()))
                .forEach(pack -> deployer.deploy(pack.service(), pack.deployment(), pack.files()));
    }
}
