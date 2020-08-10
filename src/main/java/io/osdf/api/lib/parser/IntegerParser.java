package io.osdf.api.lib.parser;

import static java.lang.Integer.parseInt;

public class IntegerParser implements ArgParser<Integer> {
    @Override
    public Integer parse(String arg) {
        return parseInt(arg);
    }
}
