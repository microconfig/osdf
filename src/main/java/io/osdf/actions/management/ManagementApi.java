package io.osdf.actions.management;

import io.osdf.api.lib.annotations.ApiCommand;
import io.osdf.api.lib.annotations.ConsoleParam;
import io.osdf.api.parameters.*;

import java.util.List;

import static io.osdf.api.lib.parameter.ParamType.REQUIRED;

public interface ManagementApi {
    @ApiCommand(description = "Deploy applications to OpenShift", order = 1)
    void deploy(@ConsoleParam(ComponentsParameter.class) List<String> components,
                @ConsoleParam(ModeParameter.class) String mode,
                @ConsoleParam(SmartDeployParameter.class) Boolean smart);

    @ApiCommand(description = "Restart services in OpenShift", order = 2)
    void restart(@ConsoleParam(ComponentsParameter.class) List<String> components);

    @ApiCommand(description = "Stop services in OpenShift", order = 3)
    void stop(@ConsoleParam(ComponentsParameter.class) List<String> components);

    @ApiCommand(description = "Delete pods", order = 4)
    void deletePod(@ConsoleParam(value = ComponentParameter.class, type = REQUIRED) String component,
                   @ConsoleParam(value = PodsParameter.class, type = REQUIRED) List<String> pods);

    @ApiCommand(description = "Delete application from cluster", order = 5)
    void clearAll(@ConsoleParam(ComponentsParameter.class) List<String> components);

    @ApiCommand(description = "Clear apps that were removed from configs", order = 6)
    void clearApps();
}
