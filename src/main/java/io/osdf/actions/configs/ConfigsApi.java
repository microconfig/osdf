package io.osdf.actions.configs;

import io.osdf.api.lib.annotations.ApiCommand;
import io.osdf.api.lib.annotations.ConsoleParam;
import io.osdf.api.parameters.ComponentParameter;
import io.osdf.api.parameters.ComponentsParameter;
import io.osdf.api.parameters.ConfigVersionParameter;
import io.osdf.api.parameters.GroupParameter;

import java.util.List;

import static io.osdf.api.lib.parameter.ParamType.REQUIRED;

public interface ConfigsApi {
    @ApiCommand(description = "Pull up-to-date configs", order = 1)
    void pull();

    @ApiCommand(description = "Set group", order = 2)
    void group(@ConsoleParam(GroupParameter.class) String group);

    @ApiCommand(description = "Set configs version", order = 3)
    void configVersion(@ConsoleParam(ConfigVersionParameter.class) String configVersion);

    @ApiCommand(description = "Change version of components", order = 4)
    void changeVersion(@ConsoleParam(value = ComponentParameter.class, type = REQUIRED) String component,
                       @ConsoleParam(value = ConfigVersionParameter.class, type = REQUIRED) String version);

    @ApiCommand(description = "Show properties difference", order = 5)
    void propertiesDiff(@ConsoleParam(ComponentsParameter.class) List<String> components);
}
