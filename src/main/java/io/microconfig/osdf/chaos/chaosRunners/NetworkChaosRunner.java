package io.microconfig.osdf.chaos.chaosRunners;

import io.microconfig.osdf.chaos.ChaosSet;
import io.microconfig.osdf.components.DeploymentComponent;
import io.microconfig.osdf.components.loader.ComponentsLoaderImpl;
import io.microconfig.osdf.deployers.Deployer;
import io.microconfig.osdf.openshift.OCExecutor;
import io.microconfig.osdf.openshift.OpenShiftProject;
import io.microconfig.osdf.paths.OSDFPaths;
import lombok.AllArgsConstructor;

import java.util.List;

import static io.microconfig.osdf.components.loader.ComponentsLoaderImpl.componentsLoader;
import static io.microconfig.osdf.deployers.NetworkChaosDeployer.chaosDeployer;
import static io.microconfig.osdf.istio.faults.Fault.fault;
import static io.microconfig.osdf.openshift.OpenShiftProject.create;

@AllArgsConstructor
public class NetworkChaosRunner implements ChaosRunner {
    private final OSDFPaths paths;
    private final OCExecutor oc;

    public static ChaosRunner networkChaosRunner(OSDFPaths paths, OCExecutor oc) {
        return new NetworkChaosRunner(paths, oc);
    }

    @Override
    public void run(List<String> components, ChaosSet chaosSet, Integer severity, Integer duration) {
        Deployer deployer = chaosDeployer(oc, fault(chaosSet.getHttpErrorCode(), severity, chaosSet.getHttpDelay(), severity));
        ComponentsLoaderImpl componentsLoader = componentsLoader(paths, components, oc);
        try (OpenShiftProject ignored = create(paths, oc).connect()) {
            componentsLoader.load(DeploymentComponent.class).forEach(deployer::deploy);
        }
    }

    @Override
    public void stop(List<String> components) {
        Deployer deployer = chaosDeployer(oc, null);
        ComponentsLoaderImpl componentsLoader = componentsLoader(paths, components, oc);
        try (OpenShiftProject ignored = create(paths, oc).connect()) {
            componentsLoader.load(DeploymentComponent.class).forEach(deployer::deploy);
        }
    }
}
