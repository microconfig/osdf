package io.microconfig.osdf.chaos.types;

import io.microconfig.osdf.cluster.cli.ClusterCLI;
import io.microconfig.osdf.exceptions.OSDFException;
import io.microconfig.osdf.paths.OSDFPaths;
import io.microconfig.osdf.service.deployment.pack.ServiceDeployPack;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static io.microconfig.osdf.service.deployment.pack.loader.DefaultServiceDeployPacksLoader.defaultServiceDeployPacksLoader;
import static io.microconfig.osdf.utils.YamlUtils.getInt;
import static io.microconfig.osdf.utils.YamlUtils.getList;
import static io.microconfig.utils.Logger.announce;
import static java.lang.Thread.currentThread;

@EqualsAndHashCode
@RequiredArgsConstructor
public class PodChaos implements Chaos {
    private static final String PARAMS = "params";
    @Getter
    private final String name;
    @Getter
    private final List<String> components;
    private final Integer timeout;
    private final Integer duration;
    private final Integer severity;

    @EqualsAndHashCode.Exclude
    Random r = new Random();

    @SuppressWarnings("unchecked")
    public static PodChaos podChaos(String name, Map<String, Object> yaml) {
        List<String> components = (List<String>) (Object) getList(yaml, "components");
        Integer timeout = getInt(yaml, PARAMS, "timeout");
        Integer duration = getInt(yaml, PARAMS, "duration");
        Integer severity = getInt(yaml, PARAMS, "severity");
        return new PodChaos(name, components, timeout, duration, severity);
    }

    public static PodChaos emptyPodChaos() {
        return new PodChaos("emptyPod", null, null, null, null);
    }

    @Override
    public void run(OSDFPaths paths, ClusterCLI cli) {
        ScheduledExecutorService service = Executors.newScheduledThreadPool(10);
        service.scheduleAtFixedRate(() -> kill(paths, cli, components, severity), 0, timeout, TimeUnit.SECONDS);

        try {
            Thread.sleep(TimeUnit.MILLISECONDS.convert(duration, TimeUnit.SECONDS));
        } catch (InterruptedException e) {
            service.shutdownNow();
            currentThread().interrupt();
            throw new OSDFException("Pod chaos execution interrupted", e);
        }
        service.shutdownNow();
    }

    @Override
    public void stop(OSDFPaths paths, ClusterCLI cli) {
        //No implementation necessary
    }

    private void kill(OSDFPaths paths, ClusterCLI cli, List<String> components, Integer severity) {
        List<ServiceDeployPack> deployPacks = defaultServiceDeployPacksLoader(paths, components, cli).loadPacks();
        deployPacks.forEach(
                pack -> pack.deployment().pods()
                        .stream()
                        .filter(pod -> r.nextInt(100) <= severity)
                        .forEach(
                                pod -> {
                                    pod.forceDelete();
                                    announce(name + ":\t " + pod.getName() + " killed.");
                                }
                        )
        );
    }

    @Override
    public void check() {
        if (duration == null || duration < 1) {
            throw new OSDFException("Duration is incorrect or missing in " + name);
        }
        if (timeout == null || timeout < 1) {
            throw new OSDFException("Timeout is incorrect or missing in " + name);
        }
        if (severity == null || severity < 1 || severity > 100) {
            throw new OSDFException("Severity is incorrect or missing in " + name);
        }
    }

    @Override
    public String type() {
        return "pod";
    }
}
