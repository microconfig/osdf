package io.osdf.actions.info.api;

import io.osdf.api.lib.annotations.Description;
import io.osdf.api.lib.annotations.Public;
import io.osdf.api.lib.annotations.parameters.Flag;
import io.osdf.api.lib.annotations.parameters.Optional;
import io.osdf.api.lib.annotations.parameters.Required;

import java.util.List;

@Public({"status", "logs", "healthcheck"})
public interface InfoApi {

    @Description("Show status info of services from OpenShift")
    @Optional(n = "components", d = "Comma separated list of components")
    @Flag(n = "healthcheck", d = "Check pods health. This option slows down status command")
    void status(List<String> components, Boolean withHealthCheck);

    @Description("Show logs of pod")
    @Required(n = "component", d = "Pod's service")
    @Optional(n = "pod", d = "Pod name or number. Pod number - order of pod in <osdf status -h> output")
    void logs(String component, String pod);

    @Description("Get healthcheck from services")
    @Optional(n = "group", d = "Microconfig components group")
    @Optional(n = "timeout", d = "Maximum waiting time for healthy response")
    void healthcheck(String group, Integer timeout);
}
