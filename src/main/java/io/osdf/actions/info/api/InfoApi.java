package io.osdf.actions.info.api;

import io.osdf.api.lib.annotations.ApiCommand;
import io.osdf.api.lib.annotations.ConsoleParam;
import io.osdf.api.lib.annotations.Hidden;
import io.osdf.api.parameters.*;

import java.util.List;

public interface InfoApi {
    @ApiCommand(description = "Show status info of services from OpenShift", order = 1)
    void status(@ConsoleParam(ComponentsParameter.class) List<String> components,
                @ConsoleParam(HealthCheckParameter.class) Boolean withHealthCheck);

    @ApiCommand(description = "Show logs of pod", order = 2)
    void logs(@ConsoleParam(ComponentParameter.class) String component,
              @ConsoleParam(PodParameter.class) String pod);

    @ApiCommand(description = "Get healthcheck from services", order = 3)
    void healthcheck(@ConsoleParam(GroupParameter.class) String group,
                     @ConsoleParam(HealthcheckTimeoutParameter.class) Integer timeout);

    @Hidden
    @ApiCommand(description = "Show all running services with their versions")
    void showAll();
}
