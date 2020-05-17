package io.microconfig.osdf.api.declarations;

import io.microconfig.osdf.api.annotation.ApiCommand;
import io.microconfig.osdf.api.annotation.ConsoleParam;
import io.microconfig.osdf.api.parameter.ComponentParameter;
import io.microconfig.osdf.api.parameter.ComponentsParameter;
import io.microconfig.osdf.api.parameter.ConfigVersionParameter;

import java.util.List;

import static io.microconfig.osdf.parameters.ParamType.REQUIRED;

public interface ComponentsApi {
    @ApiCommand(description = "Show properties difference", order = 1)
    void propertiesDiff(@ConsoleParam(ComponentsParameter.class) List<String> components);

    @ApiCommand(description = "Change version of components", order = 2)
    void changeVersion(@ConsoleParam(value = ComponentParameter.class, type = REQUIRED) String component,
                       @ConsoleParam(value = ConfigVersionParameter.class, type = REQUIRED) String version);
}
