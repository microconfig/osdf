package io.osdf.api.lib.argparsers;

import static java.lang.Integer.parseInt;

public class IntegerParser implements ArgParser<Integer> {
    @Override
    public Integer parse(String arg) {
        if (arg == null) return null;
        return parseInt(arg);
    }
}
