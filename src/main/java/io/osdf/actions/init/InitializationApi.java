package io.osdf.actions.init;

import io.osdf.api.lib.annotations.Description;
import io.osdf.api.lib.annotations.Arg;
import io.osdf.api.parsers.CredentialsParser;
import io.osdf.api.parsers.NexusArtifactParser;
import io.osdf.common.Credentials;
import io.osdf.common.nexus.NexusArtifact;

import java.nio.file.Path;

public interface InitializationApi {
    @Description("Set config parameters")
    @Arg(optional = "env", d = "Env name")
    @Arg(optional = "pv/projectVersion", d = "Project version")
    void configs(String env, String projVersion);

    @Description("Initialize git configs")
    @Arg(optional = "url", d = "Url with credentials")
    @Arg(optional = "version", d = "Git branch or tag")
    void gitConfigs(String url, String branchOrTag);

    @Description("Initialize nexus configs")
    @Arg(optional = "url", d = "Content url")
    @Arg(optional = "artifact", d = "Format - group:artifact:version or group:artifact:version:classifier", p = NexusArtifactParser.class)
    @Arg(optional = "credentials", d = "Format - user:pass", p = CredentialsParser.class)
    void nexusConfigs(String url, NexusArtifact artifact, Credentials credentials);

    @Description("Initialize local configs")
    @Arg(optional = "path", d = "Path to folder with configs. Must contain repo folder")
    @Arg(optional = "version", d = "Configs version")
    void localConfigs(Path path, String version);

    @Description("Set OpenShift credentials")
    @Arg(optional = "credentials", d = "Format - user:pass", p = CredentialsParser.class)
    @Arg(optional = "token", d = "Login using token")
    @Arg(flag ="login", d = "If set, try to login. Otherwise credentials will only be saved.")
    void openshift(Credentials credentials, String token, Boolean loginImmediately);

    @Description("Set Kubernetes credentials")
    @Arg(required = "credentials", d = "Format - user:pass", p = CredentialsParser.class)
    void kubernetes(Credentials credentials);

    @Description("Set registry credentials")
    @Arg(required = "url", d = "Registry host")
    @Arg(required = "credentials", d = "Format - user:pass", p = CredentialsParser.class)
    void registry(String url, Credentials credentials);
}
