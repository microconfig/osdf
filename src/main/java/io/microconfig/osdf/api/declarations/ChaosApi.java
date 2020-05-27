package io.microconfig.osdf.api.declarations;

import io.microconfig.osdf.api.annotation.ApiCommand;
import io.microconfig.osdf.api.annotation.ConsoleParam;
import io.microconfig.osdf.api.parameter.ChaosExperimentPlanPathParameter;

import java.nio.file.Path;

import static io.microconfig.osdf.parameters.ParamType.REQUIRED;

public interface ChaosApi {

    @ApiCommand(description = "Start chaos-plan", order = 1)
    void runChaosExperiment(@ConsoleParam(value = ChaosExperimentPlanPathParameter.class, type = REQUIRED) Path planPath);

    @ApiCommand(description = "Stop all chaos experiments", order = 2)
    void stopChaos();
}
