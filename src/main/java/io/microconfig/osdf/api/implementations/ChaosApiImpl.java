package io.microconfig.osdf.api.implementations;

import io.microconfig.osdf.api.declarations.ChaosApi;
import io.microconfig.osdf.api.declarations.ManagementApi;
import io.microconfig.osdf.commands.KillPodsCommand;
import io.microconfig.osdf.commands.NetworkChaosCommand;
import io.microconfig.osdf.commands.StartIoStressCommand;
import io.microconfig.osdf.commands.StopIoStressCommand;
import io.microconfig.osdf.openshift.OCExecutor;
import io.microconfig.osdf.paths.OSDFPaths;
import lombok.AllArgsConstructor;

import java.util.List;

import static io.microconfig.osdf.deployers.NetworkChaosDeployer.chaosDeployer;
import static io.microconfig.osdf.istio.faults.Fault.faultFromArgs;

@AllArgsConstructor
public class ChaosApiImpl implements ChaosApi {
    private final OSDFPaths paths;
    private final OCExecutor oc;

    public static ChaosApi chaosApi(OSDFPaths paths, OCExecutor oc) {
        return new ChaosApiImpl(paths, oc);
    }

    @Override
    public void startNetworkChaos(String faultType, Integer chaosSeverity, List<String> components) {
        new NetworkChaosCommand(paths, oc, chaosDeployer(oc, faultFromArgs(faultType, chaosSeverity))).run(components);
    }

    @Override
    public void stopNetworkChaos(List<String> components) {
        new NetworkChaosCommand(paths, oc, chaosDeployer(oc, null)).run(components);
    }

    @Override
    public void startIoChaos(Integer chaosSeverity, Integer duration, List<String> components) {
        new StartIoStressCommand(paths, oc, duration, chaosSeverity).run(components);
    }

    @Override
    public void stopIoChaos(List<String> components) {
        new StopIoStressCommand(paths, oc).run(components);
    }

    @Override
    public void startPodChaos(Integer chaosSeverity, List<String> components) {
        new KillPodsCommand(paths, oc, chaosSeverity).run(components);
    }

}
