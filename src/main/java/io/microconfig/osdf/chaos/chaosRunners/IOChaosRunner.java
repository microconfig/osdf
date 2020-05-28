package io.microconfig.osdf.chaos.chaosRunners;

import io.microconfig.osdf.chaos.ChaosSet;
import io.microconfig.osdf.cluster.cli.ClusterCLI;
import io.microconfig.osdf.cluster.pod.Pod;
import io.microconfig.osdf.paths.OSDFPaths;
import io.microconfig.osdf.service.deployment.pack.ServiceDeployPack;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.Random;

import static io.microconfig.osdf.service.deployment.pack.loader.DefaultServiceDeployPacksLoader.defaultServiceDeployPacksLoader;
import static io.microconfig.utils.Logger.announce;

@AllArgsConstructor
public class IOChaosRunner implements ChaosRunner {
    private final OSDFPaths paths;
    private final ClusterCLI cli;
    private final Random r = new Random();

    public static ChaosRunner ioChaosRunner(OSDFPaths paths, ClusterCLI oc) {
        return new IOChaosRunner(paths, oc);
    }


//    public void run(List<String> components, ChaosSet chaosSet, Integer severity, Integer duration) {
//        try (OpenShiftProject ignored = create(paths, oc).connect()) {
//            componentsLoader(paths, components, oc)
//                    .load(DeploymentComponent.class)
//                    .forEach(component -> startStress(component, severity, chaosSet.getIoStressTimeout()));
//        }
//    }


    @Override
    public void run(List<String> components, ChaosSet chaosSet, Integer severity, Integer duration) {
        List<ServiceDeployPack> deployPacks = defaultServiceDeployPacksLoader(paths, components, cli).loadPacks();
        deployPacks.forEach(pack -> startStress2(pack, severity, chaosSet.getIoStressTimeout()));
    }

    private void startStress2(ServiceDeployPack pack, int chaosChance, int timeout) {
        pack.deployment().pods()
                .stream()
                .filter(Pod::checkStressContainer)
                .filter(pod -> r.nextInt(100) <= chaosChance)
                .forEach(
                        pod -> {
                            cli.executeAndForget("exec " + pod.getName() + " -c stress-sidecar -- /bin/sh -c \"cd /fs; stress-ng --all 0 -t "
                                    + timeout + "s --class io\"");
                            announce("IO chaos started in " + pod.getName());
                        }
                );
    }


    @Override
    public void stop(List<String> components) {
        List<ServiceDeployPack> deployPacks = defaultServiceDeployPacksLoader(paths, components, cli).loadPacks();
        deployPacks.forEach(this::stopStress2);
    }

    private void stopStress2(ServiceDeployPack pack) {
        pack.deployment().pods()
                .stream()
                .filter(Pod::checkStressContainer)
                .forEach(this::killSidecar);
    }
//todo

//    @Override
//    public void stop(List<String> components) {
//        try (OpenShiftProject ignored = create(paths, oc).connect()) {
//
//            componentsLoader(paths, components, oc)
//                    .load(DeploymentComponent.class)
//                    .forEach(this::stopStress);
//        }
//    }

//    private void startStress(DeploymentComponent component, int chaosChance, int timeout) {
//        component.pods()
//                .stream()
//                .filter(Pod::checkStressContainer)
//                .filter(pod -> r.nextInt(100) <= chaosChance)
//                .forEach(pod -> {
//                    oc.executeAndForget("oc exec " + pod.getName() + " -c stress-sidecar -- /bin/sh -c \"cd /fs; stress-ng --all 0 -t "
//                            + timeout + "s --class io\"");
//                    announce("IO chaos started in " + pod.getName());
//                });
//    }


//
//    private void stopStress(DeploymentComponent component) {
//        component.pods()
//                .stream()
//                .filter(Pod::checkStressContainer)
//                .forEach(this::killSidecar);
//    }

    private void killSidecar(Pod pod) {
        cli.execute("exec " + pod.getName() + " -c stress-sidecar -- /bin/sh -c \"kill 1\"");
        announce("IO chaos stopped in " + pod.getName());
    }
}
