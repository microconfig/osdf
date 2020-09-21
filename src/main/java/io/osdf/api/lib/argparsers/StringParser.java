package io.osdf.api.lib.argparsers;

public class StringParser implements ArgParser<String> {
    @Override
    public String parse(String arg) {
        return arg;
    }
}
