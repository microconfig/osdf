package io.osdf.actions.chaos.assaults;

import io.osdf.core.connection.cli.ClusterCli;
import io.osdf.settings.paths.OsdfPaths;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static io.osdf.actions.chaos.assaults.IstioAssault.istioAssault;
import static io.osdf.actions.chaos.assaults.PodsAssault.podsAssault;

@RequiredArgsConstructor
public class AssaultsLoader {
    private final ClusterCli cli;
    private final OsdfPaths paths;

    public static AssaultsLoader assaultsLoader(ClusterCli cli, OsdfPaths paths) {
        return new AssaultsLoader(cli, paths);
    }

    public List<Assault> load(Map<String, Object> description) {
        List<Assault> assaults = new ArrayList<>();
        if (description.containsKey("istio")) {
            assaults.add(istioAssault(description.get("istio"), cli, paths));
        }
        if (description.containsKey("killPods")) {
            assaults.add(podsAssault(description.get("killPods"), cli, paths));
        }
        return assaults;
    }
}
