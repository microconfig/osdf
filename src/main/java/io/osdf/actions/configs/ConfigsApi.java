package io.osdf.actions.configs;

import io.osdf.api.lib.annotations.ApiCommand;
import io.osdf.api.lib.annotations.ConsoleParam;
import io.osdf.api.lib.annotations.Hidden;
import io.osdf.api.parameters.*;
import io.osdf.api.lib.annotations.Description;
import io.osdf.api.lib.annotations.parameters.Optional;
import io.osdf.api.lib.annotations.parameters.Required;

import java.util.List;

public interface ConfigsApi {
    @Description("Pull up-to-date configs")
    void pull();

    @Description("Set env")
    @Required(n = "env", d = "Env name")
    void env(String env);

    @Description("Set group")
    @Required(n = "group", d = "Group name")
    void group(String group);

    @Description("Set project and configs versions")
    @Optional(n = "cv/configVersion", d = "Config version")
    @Optional(n = "pv/projectVersion", d = "Project version")
    @Optional(n = "component", d = "If specified, only this component will be affected")
    void versions(String configVersion, String projectVersion, String app);

    @ApiCommand(description = "Show properties difference", order = 5)
    void diff(@ConsoleParam(ComponentsParameter.class) List<String> components);

    @Hidden
    @ApiCommand(description = "Lists all secrets, required by apps", order = 6)
    void requiredSecrets(@ConsoleParam(ComponentsParameter.class) List<String> components);
}
