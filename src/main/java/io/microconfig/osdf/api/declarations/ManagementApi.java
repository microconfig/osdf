package io.microconfig.osdf.api.declarations;

import io.microconfig.osdf.api.annotation.ApiCommand;
import io.microconfig.osdf.api.annotation.ConsoleParam;
import io.microconfig.osdf.api.annotation.Hidden;
import io.microconfig.osdf.api.parameter.*;

import java.util.List;

import static io.microconfig.osdf.parameters.ParamType.REQUIRED;

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
    @ApiCommand(description = "Delete deployment configs for given version", order = 5)
    void clearDeployments(@ConsoleParam(value = ConfigVersionParameter.class, type = REQUIRED) String version);

    @Hidden
    @ApiCommand(description = "Delete service from cluster", order = 10)
    void delete(@ConsoleParam(ComponentsParameter.class) List<String> components);
}
