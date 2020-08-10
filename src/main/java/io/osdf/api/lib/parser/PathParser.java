package io.osdf.api.lib.parser;

import java.nio.file.Path;

import static java.nio.file.Path.of;

public class PathParser implements ArgParser<Path> {
    @Override
    public Path parse(String arg) {
        return of(arg);
    }
}
