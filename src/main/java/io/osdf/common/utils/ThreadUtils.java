package io.osdf.common.utils;

import io.osdf.common.exceptions.PossibleBugException;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.function.Supplier;

import static java.lang.Math.max;
import static java.lang.System.currentTimeMillis;
import static java.lang.Thread.currentThread;
import static java.lang.Thread.sleep;

public class ThreadUtils {
    public static void sleepSec(long sec) {
        sleepMs(sec * 1_000);
    }

    public static void sleepMs(long ms) {
        try {
            sleep(max(0, ms));
        } catch (InterruptedException e) {
            currentThread().interrupt();
            throw new PossibleBugException("Error during sleep", e);
        }
    }

    public static long calcSecFrom(long startTime) {
        return (currentTimeMillis() - startTime) / 1000;
    }

    public static <T> T runInParallel(int parallelism, Supplier<? extends T> supplier) {
        ForkJoinPool forkJoinPool = null;
        try {
            forkJoinPool = new ForkJoinPool(parallelism == 0 ? 1 : parallelism);
            return forkJoinPool.submit(supplier::get).get();
        } catch (InterruptedException | ExecutionException | RuntimeException e) {
            currentThread().interrupt();

            Throwable cause = e;
            while(cause.getCause() != null && cause.getCause() != cause) {
                cause = cause.getCause();
            }
            throw new PossibleBugException("Error during parallel execution: " + cause.getClass().getSimpleName() + " " + cause.getMessage());
        } finally {
            if (forkJoinPool != null) {
                forkJoinPool.shutdown();
            }
        }
    }
}
