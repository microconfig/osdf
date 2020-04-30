package io.microconfig.osdf.api.declarations;

import io.microconfig.osdf.api.annotation.ApiCommand;
import io.microconfig.osdf.api.annotation.ConsoleParam;
import io.microconfig.osdf.api.parameter.ComponentParameter;
import io.microconfig.osdf.api.parameter.ComponentsParameter;
import io.microconfig.osdf.api.parameter.HealthCheckParameter;
import io.microconfig.osdf.api.parameter.PodParameter;

import java.util.List;

public interface InfoApi {
    @ApiCommand(description = "Show logs of pod", order = 1)
    void logs(@ConsoleParam(ComponentParameter.class) String component,
              @ConsoleParam(PodParameter.class) String pod);

    @ApiCommand(description = "Show status info of services from OpenShift", order = 2)
    void status(@ConsoleParam(ComponentsParameter.class) List<String> components,
                @ConsoleParam(HealthCheckParameter.class) Boolean withHealthCheck);
}
