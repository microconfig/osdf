package io.microconfig.osdf.api.v2.apis;

import io.microconfig.osdf.api.annotation.ApiCommand;
import io.microconfig.osdf.api.annotation.ConsoleParam;
import io.microconfig.osdf.api.parameter.ClearStateParameter;
import io.microconfig.osdf.api.parameter.NoBashRcParameter;

public interface InstallApi {
    @ApiCommand(description = "Install osdf commandline tool", order = 1)
    void install(@ConsoleParam(NoBashRcParameter.class) Boolean noBashRc,
                 @ConsoleParam(ClearStateParameter.class) Boolean clearState);
}
