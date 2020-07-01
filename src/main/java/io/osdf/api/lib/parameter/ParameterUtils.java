package io.osdf.api.lib.parameter;

import io.osdf.common.exceptions.OSDFException;

import java.util.List;

import static io.osdf.common.utils.StringUtils.castToInteger;
import static java.util.stream.Collectors.toUnmodifiableList;
import static java.util.stream.Stream.of;

public class ParameterUtils {
    public static List<String> toList(String s) {
        if (s == null) return null;
        return of(s.split(","))
                .collect(toUnmodifiableList());
    }

    public static Integer toInteger(String s) {
        if (s == null) return null;
        Integer weight = castToInteger(s);
        if (weight == null) throw new OSDFException("Invalid integer format " + s);
        return weight;
    }
}
