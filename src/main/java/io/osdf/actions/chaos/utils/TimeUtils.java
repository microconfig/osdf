package io.osdf.actions.chaos.utils;

import io.osdf.common.exceptions.OSDFException;

import java.time.LocalDateTime;
import java.util.Map;

import static java.lang.Integer.parseInt;
import static java.time.Instant.ofEpochMilli;
import static java.time.LocalDateTime.ofInstant;
import static java.util.Map.of;
import static java.util.TimeZone.getDefault;

public class TimeUtils {
    private final static Map<String, Integer> DURATION_SUFFIXES = of(
            "h", 60 * 60,
            "m", 60,
            "s", 1
    );

    public static int durationFromString(String durationString) {
        return DURATION_SUFFIXES
                .entrySet().stream()
                .map(entry -> parseDuration(durationString, entry.getKey(), entry.getValue()))
                .filter(duration -> duration > 0)
                .findFirst()
                .orElseThrow(() -> new OSDFException("Unknown duration format"));
    }

    public static LocalDateTime fromTimestamp(long timestamp) {
        return ofInstant(ofEpochMilli(timestamp), getDefault().toZoneId());
    }

    private static int parseDuration(String durationString, String suffix, int multiplier) {
        if (durationString.toLowerCase().endsWith(suffix)) {
            return parseInt(durationString.substring(0, durationString.length() - 1)) * multiplier;
        }
        return -1;
    }
}
