package io.osdf.actions.chaos;

import io.osdf.actions.chaos.assaults.Assault;
import io.osdf.actions.chaos.events.EventStorage;
import io.osdf.actions.chaos.scenario.Scenario;
import io.osdf.actions.chaos.state.ChaosStateManager;
import io.osdf.common.yaml.YamlObject;
import io.osdf.core.connection.cli.ClusterCli;
import io.osdf.core.local.component.ComponentDir;
import io.osdf.settings.paths.OsdfPaths;
import lombok.RequiredArgsConstructor;

import static io.osdf.actions.chaos.assaults.AssaultsLoader.assaultsLoader;
import static io.osdf.actions.chaos.events.EventStorageImpl.eventStorage;
import static io.osdf.actions.chaos.events.listeners.LoggerEventListener.logger;
import static io.osdf.actions.chaos.runner.ChaosRunner.chaosRunner;
import static io.osdf.actions.chaos.scenario.ScenarioLoader.scenarioLoader;
import static io.osdf.actions.chaos.state.ChaosPhase.FINISHED;
import static io.osdf.actions.chaos.state.ChaosStateManager.chaosStateManager;
import static io.osdf.actions.chaos.state.ChaosStatePrinter.chaosStatePrinter;
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
        ComponentDir scenarioComponent = componentsLoader().loadOne(component, componentsFinder(paths.componentsPath()));
        ChaosStateManager chaosStateManager = chaosStateManager(paths).init(scenarioComponent);
        ChaosContext chaosContext = chaosContext(chaosStateManager);

        Scenario scenario = scenarioLoader(chaosContext).load(scenarioComponent);
        chaosRunner(chaosContext).run(scenario);
        chaosStateManager.setState(FINISHED);
    }

    @Override
    public void state() {
        chaosStatePrinter(chaosStateManager(paths)).print();
    }

    @Override
    public void clearAssaults() {
        ChaosStateManager chaosStateManager = chaosStateManager(paths);
        ChaosContext chaosContext = chaosContext(chaosStateManager);
        YamlObject scenario = chaosStateManager.scenario();

        assaultsLoader(chaosContext)
                .load(scenario.get("scenario.assaults"))
                .forEach(Assault::clear);
    }

    private ChaosContext chaosContext(ChaosStateManager chaosStateManager) {
        EventStorage storage = eventStorage().with(logger(paths.chaos()));
        return ChaosContext.builder()
                .cli(cli)
                .paths(paths)
                .chaosStateManager(chaosStateManager)
                .eventStorage(storage)
                .build();
    }
}
