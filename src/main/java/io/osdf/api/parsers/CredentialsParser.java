package io.osdf.api.parsers;

import io.osdf.api.lib.argparsers.ArgParser;
import io.osdf.common.Credentials;

import static io.osdf.common.Credentials.of;

public class CredentialsParser implements ArgParser<Credentials> {
    @Override
    public Credentials parse(String arg) {
        if (arg == null) return null;
        return of(arg);
    }
}
