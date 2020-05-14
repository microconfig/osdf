package io.microconfig.osdf.api.declarations;

import io.microconfig.osdf.api.annotation.ApiCommand;
import io.microconfig.osdf.api.annotation.ConsoleParam;
import io.microconfig.osdf.api.parameter.ChaosSeverityParameter;
import io.microconfig.osdf.api.parameter.ComponentParameter;
import io.microconfig.osdf.api.parameter.NetworkFaultParameter;
import io.microconfig.osdf.api.parameter.TimeoutParameter;

import java.util.List;

import static io.microconfig.osdf.parameters.ParamType.REQUIRED;

public interface ChaosApi {

    @ApiCommand(description = "Inject network fault into components", order = 17)
    void startNetworkChaos(@ConsoleParam(value = NetworkFaultParameter.class, type = REQUIRED) String faultType,
                           @ConsoleParam(value = ChaosSeverityParameter.class, type = REQUIRED) Integer chaosSeverity,
                           @ConsoleParam(value = ComponentParameter.class) List<String> components);

    @ApiCommand(description = "Remove network fault from components", order = 18)
    void stopNetworkChaos(@ConsoleParam(value = ComponentParameter.class) List<String> components);

    @ApiCommand(description = "Start stress-ng IO chaos in stress-sidecar", order = 19)
    void startIoChaos(@ConsoleParam(value = ChaosSeverityParameter.class, type = REQUIRED) Integer chaosSeverity,
                      @ConsoleParam(value = TimeoutParameter.class) Integer duration,
                      @ConsoleParam(value = ComponentParameter.class) List<String> components);

    @ApiCommand(description = "Stop stress-ng IO chaos in stress-sidecar", order = 20)
    void stopIoChaos(@ConsoleParam(value = ComponentParameter.class) List<String> components);

    @ApiCommand(description = "Kill random pods of component", order = 21)
    void startPodChaos(@ConsoleParam(value = ChaosSeverityParameter.class, type = REQUIRED) Integer chaosSeverity,
                       @ConsoleParam(value = ComponentParameter.class) List<String> components);
}
