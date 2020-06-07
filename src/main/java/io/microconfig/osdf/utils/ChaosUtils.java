package io.microconfig.osdf.utils;

import io.microconfig.osdf.chaos.types.ChaosMode;

import static io.microconfig.osdf.chaos.types.ChaosMode.PERCENT;
import static java.lang.Math.floorDiv;

public class ChaosUtils {
    public static int calcLimit(int size, int severity, ChaosMode mode) {
        return mode == PERCENT ? floorDiv(size * severity, 100) : severity;
    }
}
