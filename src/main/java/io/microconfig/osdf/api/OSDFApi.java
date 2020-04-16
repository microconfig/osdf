package io.microconfig.osdf.api;

import io.microconfig.osdf.api.annotation.ApiCommand;
import io.microconfig.osdf.api.annotation.ConsoleParam;
import io.microconfig.osdf.api.annotation.Hidden;
import io.microconfig.osdf.api.parameter.*;
import io.microconfig.osdf.nexus.NexusArtifact;
import io.microconfig.osdf.state.ConfigSource;
import io.microconfig.osdf.state.Credentials;

import java.nio.file.Path;
import java.util.List;

import static io.microconfig.osdf.parameters.ParamType.REQUIRED;

public interface OSDFApi {
    @ApiCommand(description = "Install osdf commandline tool", order = 1)
    void install();

    @ApiCommand(description = "Init osdf with configs, credentials and project parameters", order = 3)
    void init(@ConsoleParam(GitUrlParameter.class) String gitUrl,
              @ConsoleParam(NexusUrlParameter.class) String nexusUrl,
              @ConsoleParam(ConfigsNexusArtifactParameter.class) NexusArtifact configsNexusArtifact,
              @ConsoleParam(LocalConfigsParameter.class) Path localConfigs,
              @ConsoleParam(ConfigSourceParameter.class) ConfigSource configSource,

              @ConsoleParam(OpenShiftCredentialsParameter.class) Credentials openShiftCredentials,
              @ConsoleParam(NexusCredentialsParameter.class) Credentials nexusCredentials,

              @ConsoleParam(EnvParameter.class) String env,
              @ConsoleParam(ConfigVersionParameter.class) String configVersion,
              @ConsoleParam(GroupParameter.class) String group,
              @ConsoleParam(ProjectVersionParameter.class) String projVersion,
              @ConsoleParam(ComponentsParameter.class) List<String> components);

    @ApiCommand(description = "Deploy services to OpenShift", order = 5)
    void deploy(@ConsoleParam(ComponentsParameter.class) List<String> components,
                @ConsoleParam(ModeParameter.class) String mode,
                @ConsoleParam(WaitParameter.class) Boolean wait);

    @Hidden
    @ApiCommand(description = "Set up routing rules", order = 6)
    void route(@ConsoleParam(value = ComponentParameter.class, type = REQUIRED) String component,
               @ConsoleParam(value = RoutingRuleParameter.class, type = REQUIRED) String rule);

    @ApiCommand(description = "Show status info of services from OpenShift", order = 6)
    void status(@ConsoleParam(ComponentsParameter.class) List<String> components,
                @ConsoleParam(HealthCheckParameter.class) Boolean withHealthCheck);

    @ApiCommand(description = "Restart components in OpenShift", order = 7)
    void restart(@ConsoleParam(ComponentsParameter.class) List<String> components);

    @ApiCommand(description = "Stop components in OpenShift", order = 8)
    void stop(@ConsoleParam(ComponentsParameter.class) List<String> components);

    @ApiCommand(description = "Delete components from OpenShift", order = 9)
    void delete(@ConsoleParam(ComponentsParameter.class) List<String> components);

    @ApiCommand(description = "Show current osdf setup info", order = 10)
    void state();

    @ApiCommand(description = "Show pods info", order = 11)
    void pods(@ConsoleParam(ComponentsParameter.class) List<String> components);

    @ApiCommand(description = "Delete pods", order = 12)
    void deletePod(@ConsoleParam(value = ComponentParameter.class, type = REQUIRED) String component,
                   @ConsoleParam(value = PodsParameter.class, type = REQUIRED) List<String> pods);

    @ApiCommand(description = "Show logs of pod", order = 13)
    void logs(@ConsoleParam(ComponentParameter.class) String component,
              @ConsoleParam(PodParameter.class) String pod);

    @ApiCommand(description = "Show properties difference", order = 14)
    void propertiesDiff(@ConsoleParam(ComponentsParameter.class) List<String> components);

    @ApiCommand(description = "Update osdf script", order = 15)
    void update();

    @ApiCommand(description = "Show help", order = 16)
    void help(@ConsoleParam(value = CommandParameter.class, type = REQUIRED) String command);

    @ApiCommand(description = "Show all prerequisites for osdf", order = -1)
    void howToStart();
}
