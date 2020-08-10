package io.osdf.actions.management;

import io.osdf.api.lib.annotations.Description;
import io.osdf.api.lib.annotations.Public;
import io.osdf.api.lib.annotations.parameters.Flag;
import io.osdf.api.lib.annotations.parameters.Optional;
import io.osdf.api.lib.annotations.parameters.Required;

import java.util.List;

@Public({"deploy", "restart", "stop", "deletePod", "clearAll", "clearApps"})
public interface ManagementApi {

    @Description("Deploy applications to OpenShift")
    @Optional(n = "components", d = "Comma separated list of components")
    @Flag(n = "smart", d = "If true, osdf will not redeploy unchanged services or rerun unchanged jobs")
    void deploy(List<String> components, String mode, Boolean smart);

    @Description("Restart services in OpenShift")
    @Optional(n = "components", d = "Comma separated list of components")
    void restart(List<String> components);

    @Description("Stop services in OpenShift")
    @Optional(n = "components", d = "Comma separated list of components")
    void stop(List<String> components);

    @Description("Delete pods")
    @Required(n = "component", d = "Pod's service")
    @Required(n = "pods", d = "Comma separated list of pods names or their numbers. Pod number - order of pod in <osdf status -h> output")
    void deletePod(String component, List<String> pods);

    @Description("Delete application from cluster")
    @Optional(n = "components", d = "Comma separated list of components")
    void clearAll(List<String> components);

    @Description("Clear apps that were removed from configs")
    void clearApps();
}
