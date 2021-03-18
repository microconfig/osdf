package io.osdf.actions.chaos.scenario;

import io.osdf.actions.chaos.assaults.Assault;
import io.osdf.actions.chaos.checks.Checker;
import io.osdf.common.exceptions.OSDFException;
import io.osdf.common.yaml.YamlObject;
import io.osdf.core.connection.cli.ClusterCli;
import io.osdf.core.local.component.ComponentDir;
import io.osdf.settings.paths.OsdfPaths;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;

import static io.osdf.actions.chaos.assaults.AssaultsLoader.assaultsLoader;
import static io.osdf.actions.chaos.checks.CheckersLoader.checkersLoader;
import static io.osdf.actions.chaos.utils.TimeUtils.durationFromString;
import static io.osdf.common.yaml.YamlObject.yaml;
import static java.lang.Integer.parseInt;
import static java.util.Map.of;

@RequiredArgsConstructor
public class ScenarioLoader {
    private final ClusterCli cli;
    private final OsdfPaths paths;
    
    public static ScenarioLoader scenarioLoader(ClusterCli cli, OsdfPaths paths) {
        return new ScenarioLoader(cli, paths);
    }

    public Scenario load(ComponentDir componentDir) {
        YamlObject scenario = yaml(componentDir.getPath("application.yaml"));
        int durationInSec = durationFromString(scenario.get("scenario.duration"));
        int warmupInSec = durationFromString(scenario.get("scenario.warmup"));
        List<Assault> assaults = assaultsLoader(cli, paths).load(scenario.get("scenario.assaults"));
        List<Checker> checkers = checkersLoader(cli, paths).load(scenario.get("scenario.checks"));
        return new Scenario(durationInSec, warmupInSec, assaults, checkers);
    }
}
