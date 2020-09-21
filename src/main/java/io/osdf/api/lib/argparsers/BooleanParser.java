package io.osdf.api.lib.argparsers;

import static java.lang.Boolean.valueOf;

public class BooleanParser implements ArgParser<Boolean> {
    @Override
    public Boolean parse(String arg) {
        if (arg == null) return null;
        return valueOf(arg);
    }
}
