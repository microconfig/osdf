package io.microconfig.osdf.api.v2.impls;

import io.microconfig.osdf.api.v2.apis.InstallApi;
import io.microconfig.osdf.commands.InstallCommand;
import io.microconfig.osdf.config.OSDFPaths;
import lombok.RequiredArgsConstructor;

import static io.microconfig.osdf.state.OSDFVersion.fromJar;

@RequiredArgsConstructor
public class InstallApiImpl implements InstallApi {
    private final OSDFPaths paths;

    public static InstallApi installApi(OSDFPaths paths) {
        return new InstallApiImpl(paths);
    }

    @Override
    public void install(Boolean noBashRc, Boolean clearState) {
        new InstallCommand(paths, fromJar(), noBashRc, clearState).install();
    }
}
