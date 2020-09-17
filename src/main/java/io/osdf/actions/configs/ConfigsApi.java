package io.osdf.actions.configs;

import io.osdf.api.lib.annotations.Arg;
import io.osdf.api.lib.annotations.Description;
import io.osdf.api.lib.annotations.Public;

import java.util.List;

@Public({"pull", "env", "group", "versions", "diff"})
public interface ConfigsApi {
    @Description("Pull up-to-date configs")
    void pull();

    @Description("Set env")
    @Arg(required = "env", d = "Env name")
    void env(String env);

    @Description("Set group")
    @Arg(required = "group", d = "Group name")
    void group(String group);

    @Description("Set project and configs versions")
    @Arg(optional = "cv/configVersion", d = "Config version")
    @Arg(optional = "pv/projectVersion", d = "Project version")
    @Arg(optional = "component", d = "If specified, only this component will be affected")
    void versions(String configVersion, String projectVersion, String app);

    @Description("Show properties difference")
    @Arg(optional = "components", d = "Comma separated list of components")
    void diff(List<String> components);

    @Description("Lists all secrets, required by apps")
    @Arg(optional = "components", d = "Comma separated list of components")
    void requiredSecrets(List<String> components);
}
