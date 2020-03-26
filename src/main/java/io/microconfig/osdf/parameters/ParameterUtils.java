package io.microconfig.osdf.parameters;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Stream.of;

public class ParameterUtils {
    public static List<String> toList(String s) {
        if (s == null) return null;
        return of(s.split(","))
                .collect(Collectors.toList());
    }
}
