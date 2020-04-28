package io.microconfig.osdf.api.v2.apis;

import io.microconfig.osdf.api.annotation.ApiCommand;
import io.microconfig.osdf.api.annotation.ConsoleParam;
import io.microconfig.osdf.api.parameter.*;

import java.util.List;

import static io.microconfig.osdf.parameters.ParamType.REQUIRED;

public interface ManagementApi {
    @ApiCommand(description = "Deploy services to OpenShift", order = 1)
    void deploy(@ConsoleParam(ComponentsParameter.class) List<String> components,
                @ConsoleParam(ModeParameter.class) String mode,
                @ConsoleParam(WaitParameter.class) Boolean wait);

    @ApiCommand(description = "Restart components in OpenShift", order = 2)
    void restart(@ConsoleParam(ComponentsParameter.class) List<String> components);

    @ApiCommand(description = "Stop components in OpenShift", order = 3)
    void stop(@ConsoleParam(ComponentsParameter.class) List<String> components);

    @ApiCommand(description = "Delete pods", order = 4)
    void deletePod(@ConsoleParam(value = ComponentParameter.class, type = REQUIRED) String component,
                   @ConsoleParam(value = PodsParameter.class, type = REQUIRED) List<String> pods);
}
