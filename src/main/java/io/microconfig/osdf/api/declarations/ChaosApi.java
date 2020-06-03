package io.microconfig.osdf.api.declarations;

import io.microconfig.osdf.api.annotation.ApiCommand;
import io.microconfig.osdf.api.annotation.ConsoleParam;
import io.microconfig.osdf.api.parameter.ComponentParameter;

import static io.microconfig.osdf.parameters.ParamType.REQUIRED;

public interface ChaosApi {

    @ApiCommand(description = "Start chaos experiment", order = 1)
    void runChaosExperiment(@ConsoleParam(value = ComponentParameter.class, type = REQUIRED) String componentName);

    @ApiCommand(description = "Stop all chaos experiments", order = 2)
    void stopChaos();
}
