package io.microconfig.osdf.utils;

import static java.lang.Math.max;
import static java.lang.Thread.*;

public class ThreadUtils {
    public static void sleepSec(long sec) {
        sleepMs(sec * 1_000);
    }

    public static void sleepMs(long ms) {
        try {
            sleep(max(0, ms));
        } catch (InterruptedException e) {
            currentThread().interrupt();
            throw new RuntimeException(e);
        }
    }
}
