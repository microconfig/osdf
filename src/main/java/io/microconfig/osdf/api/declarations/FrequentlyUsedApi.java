package io.microconfig.osdf.api.declarations;

import io.microconfig.osdf.api.annotation.ApiCommand;
import io.microconfig.osdf.api.annotation.ConsoleParam;
import io.microconfig.osdf.api.parameter.ConfigVersionParameter;
import io.microconfig.osdf.api.parameter.GroupParameter;

public interface FrequentlyUsedApi {
    @ApiCommand(description = "Set group", order = 1)
    void group(@ConsoleParam(GroupParameter.class) String group);

    @ApiCommand(description = "Pull up-to-date configs", order = 2)
    void pull();

    @ApiCommand(description = "Set configs version", order = 3)
    void configVersion(@ConsoleParam(ConfigVersionParameter.class) String configVersion);
}
