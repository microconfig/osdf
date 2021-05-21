package io.osdf.actions.chaos.scenario;

import io.osdf.actions.chaos.ChaosContext;
import io.osdf.actions.chaos.assaults.Assault;
import io.osdf.actions.chaos.checks.Checker;
import io.osdf.common.yaml.YamlObject;
import io.osdf.core.local.component.ComponentDir;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static io.osdf.actions.chaos.assaults.AssaultsLoader.assaultsLoader;
import static io.osdf.actions.chaos.checks.CheckersLoader.checkersLoader;
import static io.osdf.actions.chaos.utils.TimeUtils.durationFromString;
import static io.osdf.common.yaml.YamlObject.yaml;

@RequiredArgsConstructor
public class ScenarioLoader {
    private final ChaosContext chaosContext;

    public static ScenarioLoader scenarioLoader(ChaosContext chaosContext) {
        return new ScenarioLoader(chaosContext);
    }

    public Scenario load(ComponentDir componentDir) {
        YamlObject scenario = yaml(componentDir.getPath("application.yaml"));
        int durationInSec = durationFromString(scenario.get("scenario.duration"));
        int warmupInSec = durationFromString(scenario.get("scenario.warmup"));
        List<Assault> assaults = assaultsLoader(chaosContext).load(scenario.get("scenario.assaults"));
        List<Checker> checkers = checkersLoader(chaosContext).load(scenario.get("scenario.checks"));
        return new Scenario(durationInSec, warmupInSec, assaults, checkers);
    }
}
