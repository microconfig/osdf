package io.osdf.actions.system;

import io.osdf.api.lib.annotations.*;
import io.osdf.api.lib.annotations.parameters.Flag;
import io.osdf.api.lib.annotations.parameters.Optional;
import io.osdf.api.lib.annotations.parameters.Required;
import io.osdf.api.parameters.*;
import io.osdf.common.Credentials;
import io.osdf.settings.version.OsdfVersion;

import java.util.List;

import static io.osdf.api.lib.parameter.ParamType.REQUIRED;

@Public({"state", "update", "help"})
public interface SystemApi {
    @Description("Show current osdf setup info")
    void state();

    @Description("Update osdf")
    @Optional(n = "version", d = "OSDF version", p = OsdfVersionParser.class)
    @Optional(n = "credentials", d = "Nexus credentials", p = CredentialsParser.class)
    void update(OsdfVersion version, Credentials credentials);

    @Description("Show help")
    @Required(n = "command", d = "Osdf command")
    void help(List<String> command);

    @Description("Install osdf commandline tool")
    @Flag(n = "nb/nobashrc", d = "Do not create bashrc entry")
    @Flag(n = "clear-state", d = "Clear existing osdf installation")
    void install(Boolean noBashRc, Boolean clearState);

    @Description("Migrate osdf files")
    void migrate();
}
