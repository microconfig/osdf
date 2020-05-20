package io.microconfig.osdf.api.declarations;

import io.microconfig.osdf.api.annotation.ApiCommand;
import io.microconfig.osdf.api.annotation.ConsoleParam;
import io.microconfig.osdf.api.parameter.JmeterPlanPathParameter;
import io.microconfig.osdf.api.parameter.JmeterSlavesNumberParameter;

import java.nio.file.Path;

import static io.microconfig.osdf.parameters.ParamType.OPTIONAL;

public interface LoadTestingApi {
    @ApiCommand(description = "Run load testing", order = 1)
    void loadTest(@ConsoleParam(value = JmeterPlanPathParameter.class, type = OPTIONAL) Path jmeterPlanPath,
                  @ConsoleParam(value = JmeterSlavesNumberParameter.class, type = OPTIONAL) Integer numberOfSlaves);

}
