package io.osdf.actions.management;

import io.osdf.api.lib.annotations.Arg;
import io.osdf.api.lib.annotations.Description;
import io.osdf.api.lib.annotations.Public;

import java.util.List;

@Public({"deploy", "restart", "stop", "deletePod", "clearAll", "clearApps"})
public interface ManagementApi {
    @Description("Deploy applications to OpenShift")
    @Arg(optional = "components", d = "Comma separated list of components")
    @Arg(flag = "smart", d = "If true, osdf will not redeploy unchanged services or rerun unchanged jobs")
    void deploy(List<String> components, Boolean smart);

    @Description("Restart services in OpenShift")
    @Arg(optional = "components", d = "Comma separated list of components")
    void restart(List<String> components);

    @Description("Stop services in OpenShift")
    @Arg(optional = "components", d = "Comma separated list of components")
    void stop(List<String> components);

    @Description("Delete pods")
    @Arg(required = "component", d = "Pod's service")
    @Arg(required = "pods", d = "Comma separated list of pods names or their numbers. Pod number - order of pod in <osdf status -h> output")
    void deletePod(String component, List<String> pods);

    @Description("Delete application from cluster")
    @Arg(optional = "components", d = "Comma separated list of components")
    void clearAll(List<String> components);

    @Description("Clear apps that were removed from configs")
    void clearApps();
}
