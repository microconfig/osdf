package io.osdf.actions.chaos;

import io.osdf.actions.chaos.scenario.Scenario;
import io.osdf.common.exceptions.OSDFException;
import io.osdf.core.connection.cli.ClusterCli;
import io.osdf.core.local.component.ComponentDir;
import io.osdf.settings.paths.OsdfPaths;
import lombok.RequiredArgsConstructor;

import static io.osdf.actions.chaos.runner.ChaosRunner.chaosRunner;
import static io.osdf.actions.chaos.scenario.ScenarioLoader.scenarioLoader;
import static io.osdf.core.connection.cli.LoginCliProxy.loginCliProxy;
import static io.osdf.core.local.component.finder.MicroConfigComponentsFinder.componentsFinder;
import static io.osdf.core.local.component.loader.ComponentsLoaderImpl.componentsLoader;

@RequiredArgsConstructor
public class ChaosApiImpl implements ChaosApi {
    private final OsdfPaths paths;
    private final ClusterCli cli;

    public static ChaosApi chaosApi(OsdfPaths paths, ClusterCli cli) {
        return loginCliProxy(new ChaosApiImpl(paths, cli), cli);
    }

    @Override
    public void run(String component) {
        ComponentDir scenarioComponent = componentsLoader()
                .load(componentsFinder(paths.componentsPath()), t -> t.name().equals(component)).stream()
                .findFirst()
                .orElseThrow(() -> new OSDFException("Component " + component + " not found"));
        Scenario scenario = scenarioLoader(cli, paths).load(scenarioComponent);
        chaosRunner().run(scenario);
    }
}
