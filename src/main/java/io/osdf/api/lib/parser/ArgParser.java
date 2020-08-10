package io.osdf.api.lib.parser;

public interface ArgParser<T> {
    T parse(String arg);
}
