package io.microconfig.osdf.api.implementations;

import io.microconfig.osdf.api.declarations.ChaosApi;
import io.microconfig.osdf.commands.*;
import io.microconfig.osdf.openshift.OCExecutor;
import io.microconfig.osdf.paths.OSDFPaths;
import lombok.AllArgsConstructor;

import java.nio.file.Path;
import java.util.List;

import static io.microconfig.osdf.deployers.NetworkChaosDeployer.chaosDeployer;
import static io.microconfig.osdf.istio.faults.Fault.fault;

@AllArgsConstructor
public class ChaosApiImpl implements ChaosApi {
    private final OSDFPaths paths;
    private final OCExecutor oc;

    public static ChaosApi chaosApi(OSDFPaths paths, OCExecutor oc) {
        return new ChaosApiImpl(paths, oc);
    }

    @Override
    public void startNetworkChaos(Integer severity, Integer httpDelay, Integer httpError, List<String> components) {
        if (httpDelay == null && httpError == null) throw new RuntimeException("Specify HTTP error or delay");
        new NetworkChaosCommand(paths, oc, chaosDeployer(oc, fault(httpError, severity, httpDelay, severity))).run(components);
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

    @Override
    public void runChaosExperiment(Path pathToPlan) {
        new ChaosExperimentCommand(paths, oc, pathToPlan).run();
    }
}
