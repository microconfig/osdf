package io.microconfig.osdf.api.v2.impls;

import io.microconfig.osdf.api.v2.apis.SystemApi;
import io.microconfig.osdf.commands.HowToStartCommand;
import io.microconfig.osdf.config.OSDFPaths;
import io.microconfig.osdf.exceptions.OSDFException;
import lombok.RequiredArgsConstructor;

import static io.microconfig.osdf.commands.UpdateCommand.updateCommand;

@RequiredArgsConstructor
public class SystemApiImpl implements SystemApi {
    private final OSDFPaths paths;

    public static SystemApi systemApi(OSDFPaths paths) {
        return new SystemApiImpl(paths);
    }

    @Override
    public void update() {
        updateCommand(paths).update();
    }

    @Override
    public void help(String command) {
        throw new OSDFException("Not Implemented yet");
    }

    @Override
    public void howToStart() {
        new HowToStartCommand().show();
    }
}
