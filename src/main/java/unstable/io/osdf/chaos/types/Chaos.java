package unstable.io.osdf.chaos.types;

import io.osdf.core.connection.cli.ClusterCli;
import io.osdf.settings.paths.OsdfPaths;

import java.util.List;

import static unstable.io.osdf.chaos.types.IOChaos.emptyIoChaos;
import static unstable.io.osdf.chaos.types.NetworkChaos.emptyNetworkChaos;

public interface Chaos {
    static List<Chaos> getAllChaosImpls(OsdfPaths paths, ClusterCli cli) {
        return List.of(
                emptyIoChaos(paths, cli),
                emptyNetworkChaos(paths, cli),
                PodChaos.emptyPodChaos(paths, cli)
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