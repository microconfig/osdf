package unstable.io.osdf;

import io.osdf.api.lib.annotations.ApiCommand;
import io.osdf.api.lib.annotations.ConsoleParam;
import io.osdf.api.parameters.JmeterPlanPathParameter;
import io.osdf.api.parameters.JmeterSlavesNumberParameter;
import io.osdf.api.parameters.JmeterComponentNameParameter;

import java.nio.file.Path;

import static io.osdf.api.lib.parameter.ParamType.OPTIONAL;
import static io.osdf.api.lib.parameter.ParamType.REQUIRED;

public interface LoadTestingApi {
    @ApiCommand(description = "Run load testing", order = 1)
    void loadTest(@ConsoleParam(value = JmeterPlanPathParameter.class, type = OPTIONAL) Path jmeterPlanPath,
                  @ConsoleParam(value = JmeterComponentNameParameter.class, type = REQUIRED) String componentName,
                  @ConsoleParam(value = JmeterSlavesNumberParameter.class, type = OPTIONAL) Integer numberOfSlaves);

    @ApiCommand(description = "Find peak load", order = 2)
    void findPeakLoad(@ConsoleParam(value = JmeterComponentNameParameter.class, type = REQUIRED) String componentName,
                      @ConsoleParam(value = JmeterSlavesNumberParameter.class, type = OPTIONAL) Integer numberOfSlaves);
}
