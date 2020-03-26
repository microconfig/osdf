package io.microconfig.osdf.utils;

import static java.lang.Integer.valueOf;
import static java.lang.Math.max;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.joining;

public class StringUtils {
    public static String pad(String s, int length) {
        return s + " ".repeat(max(length - s.length(), 0));
    }

    public static String statusFormat(String name, String statusString, String info, String projectVersion, String configVersion) {
        return padStrings(name, statusString, info, projectVersion, configVersion);
    }

    public static Integer castToInteger(String s) {
        try {
            return valueOf(s);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private static String padStrings(String name, String... columns) {
        return stream(columns)
                .map(column -> pad(column, 20))
                .collect(joining("", pad(name, 50), ""));
    }
}
