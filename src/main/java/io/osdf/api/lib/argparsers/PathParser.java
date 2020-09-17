package io.osdf.api.lib.argparsers;

import java.nio.file.Path;

import static java.nio.file.Path.of;

public class PathParser implements ArgParser<Path> {
    @Override
    public Path parse(String arg) {
        if (arg == null) return null;
        return of(arg);
    }
}
