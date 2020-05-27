package io.microconfig.osdf.api.declarations;

import io.microconfig.osdf.api.annotation.ApiCommand;

public interface ChaosApi {

    @ApiCommand(description = "Start chaos experiment", order = 1)
    void runChaosExperiment();

    @ApiCommand(description = "Stop all chaos experiments", order = 2)
    void stopChaos();
}
