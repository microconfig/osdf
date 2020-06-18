package io.microconfig.osdf.chaos.types;

import io.cluster.old.cluster.cli.ClusterCli;
import io.osdf.settings.paths.OsdfPaths;

import java.util.List;

import static io.microconfig.osdf.chaos.types.IOChaos.emptyIoChaos;
import static io.microconfig.osdf.chaos.types.NetworkChaos.emptyNetworkChaos;
import static io.microconfig.osdf.chaos.types.PodChaos.emptyPodChaos;

public interface Chaos {
    static List<Chaos> getAllChaosImpls(OsdfPaths paths, ClusterCli cli) {
        return List.of(
                emptyIoChaos(paths, cli),
                emptyNetworkChaos(paths, cli),
                emptyPodChaos(paths, cli)
        );
    }

    void run();

    void stop();

    void forceStop();

    void check();

    List<String> getComponents();

    String getName();

    ChaosType type();
}