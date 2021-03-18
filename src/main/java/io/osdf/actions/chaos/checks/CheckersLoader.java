package io.osdf.actions.chaos.checks;

import io.osdf.core.connection.cli.ClusterCli;
import io.osdf.settings.paths.OsdfPaths;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static io.osdf.actions.chaos.checks.BasicChecker.basicChecker;
import static io.osdf.actions.chaos.checks.HttpChecker.httpChecker;

@RequiredArgsConstructor
public class CheckersLoader {
    private final ClusterCli cli;
    private final OsdfPaths paths;

    public static CheckersLoader checkersLoader(ClusterCli cli, OsdfPaths paths) {
        return new CheckersLoader(cli, paths);
    }

    @SuppressWarnings("unchecked")
    public List<Checker> load(Map<String, Object> description) {
        List<Checker> checkers = new ArrayList<>();
        if (description.containsKey("liveness")) {
            checkers.add(basicChecker((Map<String, Object>) description.get("liveness"), cli, paths));
        }
        if (description.containsKey("http")) {
            checkers.add(httpChecker((Map<String, Object>) description.get("http")));
        }
        return checkers;
    }
}
