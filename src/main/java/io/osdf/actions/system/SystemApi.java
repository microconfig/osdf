package io.osdf.actions.system;

import io.osdf.api.lib.annotations.ApiCommand;
import io.osdf.api.lib.annotations.ConsoleParam;
import io.osdf.api.lib.annotations.Hidden;
import io.osdf.api.parameters.*;
import io.osdf.common.Credentials;
import io.osdf.settings.version.OsdfVersion;

import java.util.List;

import static io.osdf.api.lib.parameter.ParamType.REQUIRED;

public interface SystemApi {
    @ApiCommand(description = "Show current osdf setup info", order = 1)
    void state();

    @ApiCommand(description = "Update osdf", order = 2)
    void update(@ConsoleParam(OSDFVersionParameter.class) OsdfVersion version,
                @ConsoleParam(UpdateNexusCredentials.class) Credentials credentials);

    @ApiCommand(description = "Show help", order = 3)
    void help(@ConsoleParam(value = CommandParameter.class, type = REQUIRED) List<String> command);

    @Hidden
    @ApiCommand(description = "Install osdf commandline tool")
    void install(@ConsoleParam(NoBashRcParameter.class) Boolean noBashRc,
                 @ConsoleParam(ClearStateParameter.class) Boolean clearState);

    @Hidden
    @ApiCommand(description = "Migrate osdf files")
    void migrate();
}
