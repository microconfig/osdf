package io.osdf.actions.chaos.state;

import io.osdf.actions.chaos.report.ChaosReport;
import io.osdf.actions.chaos.report.ReportReader;
import io.osdf.common.SettingsFile;
import io.osdf.common.yaml.YamlObject;
import io.osdf.core.local.component.ComponentDir;
import io.osdf.settings.paths.OsdfPaths;
import lombok.RequiredArgsConstructor;

import static io.osdf.actions.chaos.state.ChaosPhase.STARTING;
import static io.osdf.actions.chaos.utils.MapperUtils.createFromPath;
import static io.osdf.common.SettingsFile.settingsFile;
import static io.osdf.common.utils.FileUtils.*;
import static io.osdf.common.yaml.YamlObject.yaml;
import static java.lang.ProcessHandle.current;
import static java.lang.System.currentTimeMillis;

@RequiredArgsConstructor
public class ChaosStateManager {
    private final OsdfPaths paths;
    private final AssaultInfoManager assaultInfoManager;

    public static ChaosStateManager chaosStateManager(OsdfPaths paths) {
        return new ChaosStateManager(paths, new AssaultInfoManager(paths.chaos().assaultInfo()));
    }

    public ChaosStateManager init(ComponentDir scenarioComponent) {
        createDirectoryIfNotExists(paths.chaos().root());
        copy(scenarioComponent.getPath("application.yaml"), paths.chaos().scenario());
        delete(paths.chaos().report());
        delete(paths.chaos().assaultInfo());

        SettingsFile<ChaosState> chaosFile = settingsFile(ChaosState.class, paths.chaos().state());
        chaosFile.setIfNotNull(ChaosState::setComponent, scenarioComponent.name());
        chaosFile.setIfNotNull(ChaosState::setPhase, STARTING);
        chaosFile.setIfNotNull(ChaosState::setPid, current().pid());
        chaosFile.setIfNotNull(ChaosState::setStartTimeMs, currentTimeMillis());
        chaosFile.save();

        return this;
    }

    public void setState(ChaosPhase phase) {
        SettingsFile<ChaosState> chaosFile = settingsFile(ChaosState.class, paths.chaos().state());
        chaosFile.setIfNotNull(ChaosState::setPhase, phase);
        chaosFile.save();
    }

    public ChaosPaths chaosPaths() {
        return paths.chaos();
    }

    public YamlObject scenario() {
        return yaml(paths.chaos().scenario());
    }

    public ChaosState state() {
        return settingsFile(ChaosState.class, paths.chaos().state()).getSettings();
    }

    public ReportReader reportReader() {
        return ReportReader.reportReader(this);
    }

    public AssaultInfoManager assaultInfoManager() {
        return assaultInfoManager;
    }
}
