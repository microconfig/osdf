package io.osdf.api.parsers;

import io.osdf.api.lib.argparsers.ArgParser;
import io.osdf.settings.version.OsdfVersion;

import static io.osdf.settings.version.OsdfVersion.fromString;

public class OsdfVersionParser implements ArgParser<OsdfVersion> {
    @Override
    public OsdfVersion parse(String arg) {
        return fromString(arg);
    }
}
