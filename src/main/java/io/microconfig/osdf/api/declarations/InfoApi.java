package io.microconfig.osdf.api.declarations;

import io.microconfig.osdf.api.annotation.ApiCommand;
import io.microconfig.osdf.api.annotation.ConsoleParam;
import io.microconfig.osdf.api.parameter.*;

import java.util.List;

public interface InfoApi {
    @ApiCommand(description = "Show logs of pod", order = 1)
    void logs(@ConsoleParam(ComponentParameter.class) String component,
              @ConsoleParam(PodParameter.class) String pod);

    @ApiCommand(description = "Show status info of services from OpenShift", order = 2)
    void status(@ConsoleParam(ComponentsParameter.class) List<String> components,
                @ConsoleParam(HealthCheckParameter.class) Boolean withHealthCheck);

    @ApiCommand(description = "Get healthcheck from services")
    void healthcheck(@ConsoleParam(GroupParameter.class) String group,
                     @ConsoleParam(HealthcheckTimeoutParameter.class) Integer timeout);

    @ApiCommand(description = "Show all running services with their versions", order = 2)
    void showAll();
}
