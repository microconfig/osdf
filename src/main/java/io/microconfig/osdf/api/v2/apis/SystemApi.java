package io.microconfig.osdf.api.v2.apis;

import io.microconfig.osdf.api.annotation.ApiCommand;
import io.microconfig.osdf.api.annotation.ConsoleParam;
import io.microconfig.osdf.api.parameter.CommandParameter;

import static io.microconfig.osdf.parameters.ParamType.REQUIRED;

public interface SystemApi {
    @ApiCommand(description = "Update osdf script", order = 1)
    void update();

    @ApiCommand(description = "Show help", order = 2)
    void help(@ConsoleParam(value = CommandParameter.class, type = REQUIRED) String command);

    @ApiCommand(description = "Show all prerequisites for osdf", order = 3)
    void howToStart();

    @ApiCommand(description = "Show current osdf setup info", order = 4)
    void state();
}
