package io.microconfig.osdf.parameters;

import java.util.List;

import static java.util.stream.Collectors.toUnmodifiableList;
import static java.util.stream.Stream.of;

public class ParameterUtils {
    public static List<String> toList(String s) {
        if (s == null) return null;
        return of(s.split(","))
                .collect(toUnmodifiableList());
    }
}
