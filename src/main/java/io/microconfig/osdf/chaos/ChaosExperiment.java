package io.microconfig.osdf.chaos;

import io.microconfig.osdf.chaos.components.ChaosComponent;
import io.microconfig.osdf.chaos.types.Chaos;
import io.microconfig.osdf.cluster.cli.ClusterCLI;
import io.microconfig.osdf.paths.OSDFPaths;
import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static io.microconfig.osdf.chaos.validators.BasicValidator.*;
import static io.microconfig.osdf.chaos.validators.PodAndIOChaosIntersectionValidator.podAndIOChaosIntersectionCheck;
import static io.microconfig.osdf.utils.ThreadUtils.sleepSec;
import static io.microconfig.osdf.utils.YamlUtils.getMap;
import static io.microconfig.osdf.utils.YamlUtils.loadFromPath;
import static io.microconfig.utils.Logger.announce;
import static io.microconfig.utils.Logger.error;
import static java.lang.Thread.currentThread;

@RequiredArgsConstructor
public class ChaosExperiment {
    final private OSDFPaths paths;
    final private ClusterCLI cli;
    final private Set<Chaos> chaosSet;

    static public ChaosExperiment chaosExperiment(OSDFPaths paths, ClusterCLI cli, ChaosComponent component) {
        Map<String, Object> rules = getMap(loadFromPath(component.getPathToPlan()), "rules");
        Set<Chaos> chaosSet = rules.entrySet().stream().map(Chaos::chaos).collect(Collectors.toSet());
        check(chaosSet);
        return new ChaosExperiment(paths, cli, chaosSet);
    }

    public static void check(Set<Chaos> chaosSet) {
        basicCheck(chaosSet);
        checkNetworkChaosIntersections(chaosSet);
        checkPodChaosIntersections(chaosSet);
        podAndIOChaosIntersectionCheck(chaosSet);
    }

    static public void stop(OSDFPaths paths, ClusterCLI cli) {
        Chaos.getAllChaosImpls().forEach(chaos -> chaos.stop(paths, cli));
    }

    public void run() {
        announce("Launch of chaos");
        Set<Runnable> runnables = chaosSet.stream().map(this::toRunnable).collect(Collectors.toUnmodifiableSet());
        Set<Thread> threads = runnables.stream().map(Thread::new).collect(Collectors.toUnmodifiableSet());
        threads.forEach(Thread::start);
        announce("Chaos launched");

        while (threads.stream().anyMatch(Thread::isAlive)) {
            if (!checkMetrics()) {
                error("Metrics check failed");
                stopAll(threads);
            }
            sleepSec(1);
        }

        chaosSet.forEach(chaos -> chaos.stop(paths, cli));
        announce("Chaos stopped");
    }

    private void stopAll(Set<Thread> threads) {
        threads.forEach(Thread::interrupt);
        threads.forEach(thread -> {
            try {
                thread.join();
            } catch (InterruptedException e) {
                currentThread().interrupt();
                e.printStackTrace();
            }
        });
        error("All chaos threads joined");
    }

    Runnable toRunnable(Chaos chaos) {
        return () -> chaos.run(paths, cli);
    }

    //TODO implement metrics checking
    private boolean checkMetrics() {
        return true;
    }
}
