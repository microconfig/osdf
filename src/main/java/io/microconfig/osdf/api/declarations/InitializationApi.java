package io.microconfig.osdf.api.declarations;

import io.microconfig.osdf.api.annotation.ApiCommand;
import io.microconfig.osdf.api.annotation.ConsoleParam;
import io.microconfig.osdf.api.parameter.*;
import io.microconfig.osdf.nexus.NexusArtifact;
import io.microconfig.osdf.common.Credentials;

import java.nio.file.Path;

public interface InitializationApi {
    @ApiCommand(description = "Initialize git configs", order = 1)
    void gitConfigs(@ConsoleParam(GitUrlParameter.class) String url,
                    @ConsoleParam(ConfigVersionParameter.class) String branchOrTag);

    @ApiCommand(description = "Initialize nexus configs", order = 2)
    void nexusConfigs(@ConsoleParam(NexusUrlParameter.class) String url,
                      @ConsoleParam(ConfigsNexusArtifactParameter.class) NexusArtifact artifact,
                      @ConsoleParam(NexusCredentialsParameter.class) Credentials credentials);

    @ApiCommand(description = "Initialize local configs", order = 3)
    void localConfigs(@ConsoleParam(LocalConfigsParameter.class) Path path);

    @ApiCommand(description = "Set openshift credentials", order = 4)
    void openshift(@ConsoleParam(OpenShiftCredentialsParameter.class) Credentials credentials,
                   @ConsoleParam(OpenShiftTokenParameter.class) String token);

    @ApiCommand(description = "Set config parameters", order = 5)
    void configs(@ConsoleParam(EnvParameter.class) String env,
                 @ConsoleParam(ProjectVersionParameter.class) String projVersion);
}
