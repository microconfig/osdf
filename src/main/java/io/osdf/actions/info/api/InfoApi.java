package io.osdf.actions.info.api;

import io.osdf.api.lib.annotations.Description;
import io.osdf.api.lib.annotations.Public;
import io.osdf.api.lib.annotations.Arg;

import java.util.List;

@Public({"status", "logs", "healthcheck"})
public interface InfoApi {
    @Description("Show status info of services from OpenShift")
    @Arg(optional = "components", d = "Comma separated list of components")
    @Arg(flag ="healthcheck", d = "Check pods health. This option slows down status command")
    void status(List<String> components, Boolean withHealthCheck);

    @Description("Show logs of pod")
    @Arg(required = "component", d = "Pod's service")
    @Arg(optional = "pod", d = "Pod name or number. Pod number - order of pod in <osdf status -h> output")
    void logs(String component, String pod);

    @Description("Get healthcheck from services")
    @Arg(optional = "group", d = "Microconfig components group")
    @Arg(optional = "timeout", d = "Maximum waiting time for healthy response")
    void healthcheck(String group, Integer timeout);
}
