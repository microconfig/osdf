package io.osdf.actions.management;

import io.osdf.api.lib.annotations.ApiCommand;
import io.osdf.api.lib.annotations.ConsoleParam;
import io.osdf.api.lib.annotations.Hidden;
import io.osdf.api.parameters.*;

import java.util.List;

import static io.osdf.api.lib.parameter.ParamType.REQUIRED;

public interface ManagementApi {
    @ApiCommand(description = "Deploy services to OpenShift", order = 1)
    void deploy(@ConsoleParam(ComponentsParameter.class) List<String> components,
                @ConsoleParam(ModeParameter.class) String mode,
                @ConsoleParam(SmartDeployParameter.class) Boolean smart);

    @ApiCommand(description = "Restart components in OpenShift", order = 2)
    void restart(@ConsoleParam(ComponentsParameter.class) List<String> components);

    @ApiCommand(description = "Stop components in OpenShift", order = 3)
    void stop(@ConsoleParam(ComponentsParameter.class) List<String> components);

    @ApiCommand(description = "Delete pods", order = 4)
    void deletePod(@ConsoleParam(value = ComponentParameter.class, type = REQUIRED) String component,
                   @ConsoleParam(value = PodsParameter.class, type = REQUIRED) List<String> pods);

    @Hidden
    @ApiCommand(description = "Delete service from cluster")
    void delete(@ConsoleParam(ComponentsParameter.class) List<String> components);
}
