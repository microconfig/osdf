package io.microconfig.osdf.api.declarations;

import io.microconfig.osdf.api.annotation.ApiCommand;
import io.microconfig.osdf.api.annotation.ConsoleParam;
import io.microconfig.osdf.api.parameter.ComponentsParameter;
import io.microconfig.osdf.api.parameter.ModeParameter;

import java.util.List;

public interface NewManagementApi {
    @ApiCommand(description = "Deploy services to cluster", order = 1)
    void deploy(@ConsoleParam(ComponentsParameter.class) List<String> components,
                @ConsoleParam(ModeParameter.class) String mode);
}
