package io.osdf.api.lib.argparsers;

import java.util.List;

import static java.util.List.of;
import static java.util.stream.Collectors.toUnmodifiableList;

public interface ArgParser<T> {
    T parse(String arg);

    default List<T> parseList(List<String> args) {
        return args.stream()
                .map(this::parse)
                .collect(toUnmodifiableList());
    }

    default List<T> parseList(String listArg) {
        if (listArg == null) return null;
        return parseList(of(listArg.split(",")));
    }
}
