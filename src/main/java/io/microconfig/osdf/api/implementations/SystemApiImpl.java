package io.microconfig.osdf.api.implementations;

import io.microconfig.osdf.api.ApiCall;
import io.microconfig.osdf.api.ApiCallFinder;
import io.microconfig.osdf.api.MainApi;
import io.microconfig.osdf.api.declarations.SystemApi;
import io.microconfig.osdf.commands.CurrentStateCommand;
import io.microconfig.osdf.common.Credentials;
import io.microconfig.osdf.exceptions.OSDFException;
import io.microconfig.osdf.install.UpdateSettings;
import io.microconfig.osdf.paths.OSDFPaths;
import io.microconfig.osdf.settings.SettingsFile;
import io.microconfig.osdf.state.OSDFVersion;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static io.microconfig.osdf.api.ApiMethodReader.apiMethodReader;
import static io.microconfig.osdf.commands.UpdateCommand.updateCommand;
import static io.microconfig.osdf.install.migrations.AllMigrations.allMigrations;
import static io.microconfig.osdf.settings.SettingsFile.settingsFile;
import static io.microconfig.utils.Logger.warn;
import static java.lang.String.join;

@RequiredArgsConstructor
public class SystemApiImpl implements SystemApi {
    private final OSDFPaths paths;

    public static SystemApi systemApi(OSDFPaths paths) {
        return new SystemApiImpl(paths);
    }

    @Override
    public void update(OSDFVersion version, Credentials credentials) {
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
}
