package io.osdf.actions.system;

import io.osdf.api.lib.annotations.Arg;
import io.osdf.api.lib.annotations.Description;
import io.osdf.api.lib.annotations.Public;
import io.osdf.api.parsers.CredentialsParser;
import io.osdf.api.parsers.OsdfVersionParser;
import io.osdf.common.Credentials;
import io.osdf.settings.version.OsdfVersion;

import java.util.List;

@Public({"state", "update", "help"})
public interface SystemApi {
    @Description("Show current osdf setup info")
    void state();

    @Description("Update osdf")
    @Arg(optional = "version", d = "OSDF version", p = OsdfVersionParser.class)
    @Arg(optional = "credentials", d = "Nexus credentials", p = CredentialsParser.class)
    void update(OsdfVersion version, Credentials credentials);

    @Description("Show help")
    @Arg(required = "commandArgs", d = "Osdf command")
    void help(List<String> command);

    @Description("Install osdf commandline tool")
    @Arg(flag ="nb/nobashrc", d = "Do not create bashrc entry")
    @Arg(flag ="clear-state", d = "Clear existing osdf installation")
    void install(Boolean noBashRc, Boolean clearState);

    @Description("Migrate osdf files")
    void migrate();
}
