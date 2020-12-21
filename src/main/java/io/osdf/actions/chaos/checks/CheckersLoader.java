package io.osdf.actions.chaos.checks;

import io.osdf.core.connection.cli.ClusterCli;
import io.osdf.settings.paths.OsdfPaths;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static io.osdf.actions.chaos.checks.BasicChecker.basicChecker;

@RequiredArgsConstructor
public class CheckersLoader {
    private final ClusterCli cli;
    private final OsdfPaths paths;

    public static CheckersLoader checkersLoader(ClusterCli cli, OsdfPaths paths) {
        return new CheckersLoader(cli, paths);
    }

    public List<Checker> load(Map<String, Object> description) {
        List<Checker> checkers = new ArrayList<>();
        if (description.containsKey("status")) {
            checkers.add(basicChecker(cli, paths));
        }
        return checkers;
    }
}
