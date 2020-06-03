package io.microconfig.osdf.chaos.types;

import io.microconfig.osdf.cluster.cli.ClusterCLI;
import io.microconfig.osdf.cluster.pod.Pod;
import io.microconfig.osdf.exceptions.OSDFException;
import io.microconfig.osdf.paths.OSDFPaths;
import io.microconfig.osdf.service.deployment.pack.ServiceDeployPack;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.Random;

import static io.microconfig.osdf.service.deployment.pack.loader.DefaultServiceDeployPacksLoader.defaultServiceDeployPacksLoader;
import static io.microconfig.osdf.utils.ThreadUtils.sleepSec;
import static io.microconfig.osdf.utils.YamlUtils.getInt;
import static io.microconfig.osdf.utils.YamlUtils.getList;
import static io.microconfig.utils.Logger.announce;

@EqualsAndHashCode
@RequiredArgsConstructor
public class IOChaos implements Chaos {
    @Getter
    private final String name;
    @Getter
    private final List<String> components;
    private final Integer severity;
    private final Integer timeout;

    @EqualsAndHashCode.Exclude
    Random r = new Random();

    @SuppressWarnings("unchecked")
    public static IOChaos ioChaos(String name, Map<String, Object> yaml) {
        List<String> components = (List<String>) (Object) getList(yaml, "components");
        Integer severity = getInt(yaml, "params", "severity");
        Integer timeout = getInt(yaml, "params", "timeout");
        return new IOChaos(name, components, severity, timeout);
    }

    public static IOChaos emptyIoChaos() {
        return new IOChaos("EmptyIO", null, null, null);
    }

    @Override
    public void run(OSDFPaths paths, ClusterCLI cli) {
        List<ServiceDeployPack> deployPacks = defaultServiceDeployPacksLoader(paths, components, cli).loadPacks();
        deployPacks.forEach(pack -> pack.deployment().pods()
                .parallelStream()
                .filter(Pod::checkStressContainer)
                .filter(pod -> r.nextInt(100) <= severity)
                .forEach(
                        pod -> {
                            announce(name + ":\t IO chaos launching in " + pod.getName());
                            cli.execute("exec " + pod.getName() + " -c stress-sidecar -- /bin/sh -c \"cd /fs; stress-ng --all 0 -t "
                                    + timeout + "s --class io\" &");
                        }
                ));
        sleepSec(timeout);
    }

    @Override
    public void stop(OSDFPaths paths, ClusterCLI cli) {
        List<ServiceDeployPack> deployPacks = defaultServiceDeployPacksLoader(paths, components, cli).loadPacks();
        deployPacks.forEach(pack -> pack.deployment().pods()
                .stream()
                .filter(Pod::checkStressContainer)
                .forEach(pod -> {
                    cli.execute("exec " + pod.getName() + " -c stress-sidecar -- /bin/sh -c \"kill 1\"");
                    announce(name + ":\t IO chaos stopped in " + pod.getName());
                }));
    }

    @Override
    public void check() {
        if (severity == null || severity < 1 || severity > 100) {
            throw new OSDFException("Severity is incorrect or missing in " + name);
        }
        if (timeout == null || timeout < 1) {
            throw new OSDFException("Timeout is incorrect or missing in " + name);
        }
        if (components == null || components.isEmpty()) {
            throw new OSDFException("List of components is empty or missing in : " + name);
        }
    }

    @Override
    public String type() {
        return "io";
    }
}
