package io.osdf.actions.init.api;

import io.osdf.api.lib.annotations.ApiCommand;
import io.osdf.api.lib.annotations.ConsoleParam;
import io.osdf.api.parameters.*;
import io.osdf.common.Credentials;
import io.osdf.common.nexus.NexusArtifact;

import java.nio.file.Path;

import static io.osdf.api.lib.parameter.ParamType.REQUIRED;

public interface InitializationApi {
    @ApiCommand(description = "Set config parameters", order = 1)
    void configs(@ConsoleParam(EnvParameter.class) String env,
                 @ConsoleParam(ProjectVersionParameter.class) String projVersion);

    @ApiCommand(description = "Initialize git configs", order = 2)
    void gitConfigs(@ConsoleParam(GitUrlParameter.class) String url,
                    @ConsoleParam(ConfigVersionParameter.class) String branchOrTag);

    @ApiCommand(description = "Initialize nexus configs", order = 3)
    void nexusConfigs(@ConsoleParam(NexusUrlParameter.class) String url,
                      @ConsoleParam(ConfigsNexusArtifactParameter.class) NexusArtifact artifact,
                      @ConsoleParam(NexusCredentialsParameter.class) Credentials credentials);

    @ApiCommand(description = "Initialize local configs", order = 4)
    void localConfigs(@ConsoleParam(LocalConfigsParameter.class) Path path,
                      @ConsoleParam(ConfigVersionParameter.class) String version);

    @ApiCommand(description = "Set OpenShift credentials", order = 5)
    void openshift(@ConsoleParam(ClusterCredentialsParameter.class) Credentials credentials,
                   @ConsoleParam(OpenShiftTokenParameter.class) String token,
                   @ConsoleParam(LoginImmediatelyParameter.class) Boolean loginImmediately);

    @ApiCommand(description = "Set Kubernetes credentials", order = 6)
    void kubernetes(@ConsoleParam(value = ClusterCredentialsParameter.class, type = REQUIRED) Credentials credentials);

    @ApiCommand(description = "Set registry credentials", order = 7)
    void registry(@ConsoleParam(value = RegistryUrlParameter.class, type = REQUIRED) String url,
                  @ConsoleParam(value = RegistryCredentialsParameter.class, type = REQUIRED) Credentials credentials);
}
