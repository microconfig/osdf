package io.osdf.actions.chaos.checks;

import io.osdf.actions.chaos.events.EventStorage;
import io.osdf.core.connection.cli.ClusterCli;
import io.osdf.settings.paths.OsdfPaths;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static io.osdf.actions.chaos.checks.HttpChecker.httpChecker;
import static io.osdf.actions.chaos.checks.LivenessChecker.basicChecker;

@RequiredArgsConstructor
public class CheckersLoader {
    private final ClusterCli cli;
    private final OsdfPaths paths;
    private final EventStorage storage;

    public static CheckersLoader checkersLoader(ClusterCli cli, OsdfPaths paths, EventStorage storage) {
        return new CheckersLoader(cli, paths, storage);
    }

    @SuppressWarnings("unchecked")
    public List<Checker> load(Map<String, Object> description) {
        List<Checker> checkers = new ArrayList<>();
        if (description.containsKey("liveness")) {
            checkers.add(
                    basicChecker((Map<String, Object>) description.get("liveness"), cli, paths)
                            .setEventSender(storage.sender("checker"))
            );
        }
        if (description.containsKey("http")) {
            checkers.add(
                    httpChecker((Map<String, Object>) description.get("http"))
                            .setEventSender(storage.sender("checker"))
            );
        }
        return checkers;
    }
}
