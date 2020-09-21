package io.osdf.api.lib.argparsers;

import io.osdf.api.lib.ApiException;

import java.nio.file.Path;
import java.util.List;

public class DefaultParser implements ArgParser<Object> {
    private final ArgParser<?> parser;

    public DefaultParser(Class<?> argType) {
        if (String.class.isAssignableFrom(argType) || List.class.isAssignableFrom(argType)) {
            parser = new StringParser();
        } else if (Integer.class.isAssignableFrom(argType)) {
            parser = new IntegerParser();
        } else if (Path.class.isAssignableFrom(argType)) {
            parser = new PathParser();
        } else if (Boolean.class.isAssignableFrom(argType)) {
            parser = new BooleanParser();
        } else {
            throw new ApiException("Couldn't find default parser for type " + argType.getSimpleName());
        }
    }

    @Override
    public Object parse(String arg) {
        return parser.parse(arg);
    }
}
