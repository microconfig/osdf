package io.microconfig.osdf.chaos.types;

import io.microconfig.osdf.cluster.cli.ClusterCLI;
import io.microconfig.osdf.deployers.NetworkChaosDeployer;
import io.microconfig.osdf.exceptions.OSDFException;
import io.microconfig.osdf.istio.faults.Fault;
import io.microconfig.osdf.paths.OSDFPaths;
import io.microconfig.osdf.service.deployment.pack.ServiceDeployPack;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;

import static io.microconfig.osdf.deployers.NetworkChaosDeployer.chaosDeployer;
import static io.microconfig.osdf.istio.faults.Fault.fault;
import static io.microconfig.osdf.service.deployment.pack.loader.DefaultServiceDeployPacksLoader.defaultServiceDeployPacksLoader;
import static io.microconfig.osdf.utils.IstioUtils.isIstioService;
import static io.microconfig.osdf.utils.YamlUtils.getInt;
import static io.microconfig.osdf.utils.YamlUtils.getList;

@EqualsAndHashCode
@RequiredArgsConstructor
public class NetworkChaos implements Chaos {
    @Getter
    final private String name;
    @Getter
    final private List<String> components;
    final private Fault fault;

    @SuppressWarnings("unchecked")
    public static NetworkChaos networkChaos(String name, Map<String, Object> yaml) {
        List<String> components = (List<String>) (Object) getList(yaml, "components");
        Integer abortCode = getInt(yaml, "params", "http-error", "code");
        Integer abortPercentage = getInt(yaml, "params", "http-error", "percentage");
        Integer delay = getInt(yaml, "params", "http-delay", "delay");
        Integer delayPercentage = getInt(yaml, "params", "http-delay", "percentage");
        return new NetworkChaos(name, components, fault(abortCode, abortPercentage, delay, delayPercentage));
    }

    public static NetworkChaos emptyNetworkChaos() {
        return new NetworkChaos("emptyNetwork", null, null);
    }

    @Override
    public void run(OSDFPaths paths, ClusterCLI cli) {
        deploy(paths, cli, components, fault);
    }

    @Override
    public void stop(OSDFPaths paths, ClusterCLI cli) {
        deploy(paths, cli, components, null);
    }

    private void deploy(OSDFPaths paths, ClusterCLI cli, List<String> components, Fault fault) {
        NetworkChaosDeployer deployer = chaosDeployer(fault);
        List<ServiceDeployPack> deployPacks = defaultServiceDeployPacksLoader(paths, components, cli).loadPacks();
        deployPacks.parallelStream()
                .filter(pack -> isIstioService(pack.service()))
                .forEach(pack -> deployer.deploy(pack.service()));
    }

    @Override
    public void check() {
        try {
            fault.checkCorrectness();
        } catch (OSDFException e) {
            throw new OSDFException("Incorrect fault in " + name, e);
        }

        if (components == null || components.isEmpty()) {
            throw new OSDFException("List of components is empty or missing in: " + name);
        }
    }

    @Override
    public String type() {
        return "network";
    }
}
