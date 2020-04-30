package io.microconfig.osdf.api.declarations;

import io.microconfig.osdf.api.parameter.UpdateNexusCredentials;
import io.microconfig.osdf.api.annotation.ApiCommand;
import io.microconfig.osdf.api.annotation.ConsoleParam;
import io.microconfig.osdf.api.annotation.Hidden;
import io.microconfig.osdf.api.parameter.CommandParameter;
import io.microconfig.osdf.api.parameter.OSDFVersionParameter;
import io.microconfig.osdf.common.Credentials;
import io.microconfig.osdf.state.OSDFVersion;

import java.util.List;

import static io.microconfig.osdf.parameters.ParamType.REQUIRED;

public interface SystemApi {
    @ApiCommand(description = "Update osdf script", order = 1)
    void update(@ConsoleParam(OSDFVersionParameter.class) OSDFVersion version,
                @ConsoleParam(UpdateNexusCredentials.class) Credentials credentials);

    @ApiCommand(description = "Show help", order = 2)
    void help(@ConsoleParam(value = CommandParameter.class, type = REQUIRED) List<String> command);

    @ApiCommand(description = "Show current osdf setup info", order = 4)
    void state();

    @Hidden
    @ApiCommand(description = "Migrate osdf files")
    void migrate();
}
