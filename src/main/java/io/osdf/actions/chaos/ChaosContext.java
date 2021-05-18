package io.osdf.actions.chaos;

import io.osdf.actions.chaos.state.ChaosStateManager;
import io.osdf.core.connection.cli.ClusterCli;
import io.osdf.core.events.EventStorage;
import io.osdf.settings.paths.OsdfPaths;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Builder
@Accessors(fluent = true)
public class ChaosContext {
    private final ClusterCli cli;
    private final OsdfPaths paths;
    private final ChaosStateManager chaosStateManager;
    private final EventStorage eventStorage;
}
