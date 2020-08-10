package io.osdf.actions.init;

import io.osdf.api.lib.annotations.Description;
import io.osdf.api.lib.annotations.parameters.Flag;
import io.osdf.api.lib.annotations.parameters.Optional;
import io.osdf.api.lib.annotations.parameters.Required;
import io.osdf.common.Credentials;
import io.osdf.common.nexus.NexusArtifact;

import java.nio.file.Path;

public interface InitializationApi {
    @Description("Set config parameters")
    @Optional(n = "env", d = "Env name")
    @Optional(n = "pv/projectVersion", d = "Project version")
    void configs(String env, String projVersion);

    @Description("Initialize git configs")
    @Optional(n = "url", d = "Url with credentials")
    @Optional(n = "version", d = "Git branch or tag")
    void gitConfigs(String url, String branchOrTag);

    @Description("Initialize nexus configs")
    @Optional(n = "url", d = "Content url")
    @Optional(n = "artifact", d = "Format - group:artifact:version or group:artifact:version:classifier", p = NexusArtifactParser.class)
    @Optional(n = "credentials", d = "Format - user:pass", p = CredentialsParser.class)
    void nexusConfigs(String url, NexusArtifact artifact, Credentials credentials);

    @Description("Initialize local configs")
    @Optional(n = "path", d = "Path to folder with configs. Must contain repo folder")
    @Optional(n = "version", d = "Configs version")
    void localConfigs(Path path, String version);

    @Description("Set OpenShift credentials")
    @Optional(n = "credentials", d = "Format - user:pass", p = CredentialsParser.class)
    @Optional(n = "token", d = "Login using token")
    @Flag(n = "login", d = "If set, try to login. Otherwise credentials will only be saved.")
    void openshift(Credentials credentials, String token, Boolean loginImmediately);

    @Description("Set Kubernetes credentials")
    @Required(n = "credentials", d = "Format - user:pass", p = CredentialsParser.class)
    void kubernetes(Credentials credentials);

    @Description("Set registry credentials")
    @Required(n = "url", d = "Registry host")
    @Required(n = "credentials", d = "Format - user:pass", p = CredentialsParser.class)
    void registry(String url, Credentials credentials);
}
