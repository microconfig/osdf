package io.osdf.actions.system;

import io.osdf.actions.system.install.jarinstaller.JarInstaller;
import io.osdf.actions.system.state.CurrentStateCommand;
import io.osdf.actions.system.update.UpdateSettings;
import io.osdf.api.MainApi;
import io.osdf.api.lib.ApiCall;
import io.osdf.api.lib.ApiCallFinder;
import io.osdf.common.Credentials;
import io.osdf.common.SettingsFile;
import io.osdf.common.exceptions.OSDFException;
import io.osdf.settings.paths.OsdfPaths;
import io.osdf.settings.version.OsdfVersion;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static io.microconfig.utils.Logger.announce;
import static io.microconfig.utils.Logger.warn;
import static io.osdf.actions.system.install.OSDFInstaller.osdfInstaller;
import static io.osdf.actions.system.install.jarinstaller.FakeJarInstaller.fakeJarInstaller;
import static io.osdf.actions.system.install.jarinstaller.LocalJarInstaller.jarInstaller;
import static io.osdf.actions.system.install.migrations.AllMigrations.allMigrations;
import static io.osdf.actions.system.update.UpdateCommand.updateCommand;
import static io.osdf.api.lib.ApiMethodReader.apiMethodReader;
import static io.osdf.common.SettingsFile.settingsFile;
import static io.osdf.common.utils.JarUtils.isJar;
import static io.osdf.settings.paths.OsdfPaths.paths;
import static io.osdf.settings.version.OsdfVersion.fromString;
import static java.lang.String.join;

@RequiredArgsConstructor
public class SystemApiImpl implements SystemApi {
    private final OsdfPaths paths;

    public static SystemApi systemApi(OsdfPaths paths) {
        return new SystemApiImpl(paths);
    }

    @Override
    public void update(OsdfVersion version, Credentials credentials) {
        SettingsFile<UpdateSettings> updateFile = settingsFile(UpdateSettings.class, paths.settings().update());
        updateFile.setIfNotNull(UpdateSettings::setCredentials, credentials);
        updateFile.save();

        if (version != null) {
            updateCommand(paths).update(version);
        } else {
            warn("Specify version if you want to update");
        }
    }

    @Override
    public void help(List<String> command) {
        ApiCall apiCall = ApiCallFinder.finder(MainApi.class).find(command);
        if (!apiCall.getArgs().isEmpty()) throw new OSDFException("Additional arguments for method are not allowed");

        apiMethodReader(apiCall.getMethod(), join(" ", command)).printHelp();
    }

    @Override
    public void state() {
        new CurrentStateCommand(paths).show();
    }

    @Override
    public void migrate() {
        allMigrations().apply(paths);
    }

    @Override
    public void install(Boolean noBashRc, Boolean clearState) {
        if (!isJar() && paths.root().equals(paths().root())) throw new OSDFException("Installation is possible only using jar file");
        JarInstaller jarInstaller = isJar() ? jarInstaller(paths) : fakeJarInstaller(paths, fromString("1.0.0"));
        osdfInstaller(paths, jarInstaller, clearState, noBashRc).install();
        announce("Installed " + jarInstaller.version());
    }
}
