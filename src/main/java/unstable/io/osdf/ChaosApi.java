package unstable.io.osdf;

import io.osdf.api.lib.annotations.ApiCommand;
import io.osdf.api.lib.annotations.ConsoleParam;
import io.osdf.api.parameters.ComponentParameter;

import static io.osdf.api.lib.parameter.ParamType.REQUIRED;

public interface ChaosApi {

    @ApiCommand(description = "Start chaos experiment", order = 1)
    void runChaosExperiment(@ConsoleParam(value = ComponentParameter.class, type = REQUIRED) String componentName);

    @ApiCommand(description = "Stop all chaos experiments", order = 2)
    void stopChaos();
}
