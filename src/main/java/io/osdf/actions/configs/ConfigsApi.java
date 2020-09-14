package io.osdf.actions.configs;

import io.osdf.api.lib.annotations.ApiCommand;
import io.osdf.api.lib.annotations.ConsoleParam;
import io.osdf.api.lib.annotations.Hidden;
import io.osdf.api.parameters.*;

import java.util.List;

public interface ConfigsApi {
    @ApiCommand(description = "Pull up-to-date configs", order = 1)
    void pull();

    @ApiCommand(description = "Set env", order = 2)
    void env(@ConsoleParam(EnvParameter.class) String env);

    @ApiCommand(description = "Set group", order = 3)
    void group(@ConsoleParam(GroupParameter.class) String group);

    @ApiCommand(description = "Set project and configs versions", order = 4)
    void versions(@ConsoleParam(ConfigVersionParameter.class) String configVersion,
                  @ConsoleParam(ProjectVersionParameter.class) String projectVersion,
                  @ConsoleParam(ComponentParameter.class) String app);

    @ApiCommand(description = "Show properties difference", order = 5)
    void diff(@ConsoleParam(ComponentsParameter.class) List<String> components);

    @Hidden
    @ApiCommand(description = "Lists all secrets, required by apps", order = 6)
    void requiredSecrets(@ConsoleParam(ComponentsParameter.class) List<String> components);
}
