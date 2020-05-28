package io.microconfig.osdf.chaos.chaosRunners;

import io.microconfig.osdf.chaos.ChaosSet;
import io.microconfig.osdf.cluster.cli.ClusterCLI;
import io.microconfig.osdf.paths.OSDFPaths;
import io.microconfig.osdf.service.deployment.pack.ServiceDeployPack;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static io.microconfig.osdf.service.deployment.pack.loader.DefaultServiceDeployPacksLoader.defaultServiceDeployPacksLoader;
import static io.microconfig.osdf.utils.ThreadUtils.sleepSec;
import static io.microconfig.utils.Logger.announce;

@AllArgsConstructor
public class PodChaosRunner implements ChaosRunner {
    private final OSDFPaths paths;
    private final ClusterCLI cli;
    private final Random r = new Random();

    public static ChaosRunner podChaosRunner(OSDFPaths paths, ClusterCLI cli) {
        return new PodChaosRunner(paths, cli);
    }

    @Override
    public void run(List<String> components, ChaosSet chaosSet, Integer severity, Integer duration) {
        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
        service.scheduleAtFixedRate(() -> kill2(components, severity), 0, chaosSet.getKillPodTimeout(), TimeUnit.SECONDS);
        sleepSec(duration);
        service.shutdownNow();
    }

    private void kill2(List<String> components, Integer severity) {
        List<ServiceDeployPack> deployPacks = defaultServiceDeployPacksLoader(paths, components, cli).loadPacks();
        deployPacks.forEach(
                pack -> { //todo test me
                    pack.deployment().pods()
                            .stream()
                            .filter(pod -> r.nextInt(100) <= severity)
                            .forEach(
                                    pod -> {
                                        pod.forceDelete();
                                        announce(pod.getName() + " killed.");
                                    }
                            );
                }
        );
    }

    //todo
//    void kill(List<String> components, Integer severity) {
//        try (OpenShiftProject ignored = create(paths, oc).connect()) {
//            componentsLoader(paths, components, oc)
//                    .load(DeploymentComponent.class)
//                    .forEach(component -> killPods(component, severity));
//        }
//    }
//
//    private void killPods(DeploymentComponent component, int chaosChance) {
//        component.pods()
//                .stream()
//                .filter(pod -> r.nextInt(100) <= chaosChance)
//                .forEach(pod -> {
//                    pod.forceDelete();
//                    announce(pod.getName() + " killed.");
//                });
//    }

    @Override
    public void stop(List<String> components) {
        //No implementation necessary
    }
}
