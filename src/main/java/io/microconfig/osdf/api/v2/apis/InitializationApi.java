package io.microconfig.osdf.api.v2.apis;

import io.microconfig.osdf.api.annotation.ApiCommand;
import io.microconfig.osdf.api.annotation.ConsoleParam;
import io.microconfig.osdf.api.parameter.*;
import io.microconfig.osdf.nexus.NexusArtifact;
import io.microconfig.osdf.state.Credentials;
import io.microconfig.osdf.state.OpenShiftCredentials;

import java.nio.file.Path;

public interface InitializationApi {
    @ApiCommand(description = "Initialize git configs", order = 1)
    void gitConfigs(@ConsoleParam(GitUrlParameter.class) String gitUrl,
                    @ConsoleParam(ConfigVersionParameter.class) String configVersion);

    @ApiCommand(description = "Initialize nexus configs", order = 2)
    void nexusConfigs(@ConsoleParam(NexusUrlParameter.class) String nexusUrl,
                      @ConsoleParam(ConfigsNexusArtifactParameter.class) NexusArtifact configsNexusArtifact,
                      @ConsoleParam(ConfigVersionParameter.class) String configVersion,
                      @ConsoleParam(NexusCredentialsParameter.class) Credentials nexusCredentials);

    @ApiCommand(description = "Initialize local configs", order = 3)
    void localConfigs(@ConsoleParam(LocalConfigsParameter.class) Path localConfigs);

    @ApiCommand(description = "Set openshift credentials", order = 4)
    void openshift(@ConsoleParam(OpenShiftCredentialsParameter.class) OpenShiftCredentials openShiftCredentials);

    @ApiCommand(description = "Set config parameters", order = 5)
    void config(@ConsoleParam(EnvParameter.class) String env,
                @ConsoleParam(ProjectVersionParameter.class) String projVersion);

    @ApiCommand(description = "Explicitly pull new configs", order = 6)
    void pull();
}
