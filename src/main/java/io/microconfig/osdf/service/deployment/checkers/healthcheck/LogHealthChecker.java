package io.microconfig.osdf.service.deployment.checkers.healthcheck;

import io.microconfig.osdf.cluster.pod.Pod;
import lombok.RequiredArgsConstructor;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import static io.microconfig.osdf.utils.ThreadUtils.sleepSec;
import static io.microconfig.utils.Logger.info;
import static io.microconfig.utils.Logger.warn;
import static java.lang.Runtime.getRuntime;
import static java.lang.System.currentTimeMillis;

@RequiredArgsConstructor
public class LogHealthChecker implements HealthChecker {
    private final static int LOG_WAIT_LIMIT = 10;
    private final static int LOG_LINE_LIMIT = 500;

    private final String marker;
    private final int timeoutInSec;

    public static LogHealthChecker logHealthChecker(String marker, int timeoutInSec) {
        return new LogHealthChecker(marker, timeoutInSec);
    }

    public boolean check(Pod pod) {
        try {
            Process process = getRuntime().exec("oc logs -f " + pod.getName() + " -c " + pod.getComponentName());
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                long startTime = currentTimeMillis();
                StringBuilder logContent = new StringBuilder();
                boolean gotLogs = false;
                int numLines = 0;
                while (true) {
                    if (reader.ready()) {
                        gotLogs = true;
                        String str = reader.readLine();
                        numLines++;
                        if (numLines > LOG_LINE_LIMIT) {
                            warn("Line limit exceeded for " + pod.getName() + " in log healthcheck.");
                            return true;
                        }
                        logContent.append(str);
                        if (logContent.indexOf(marker) >= 0) return true;
                        if (logContent.length() > marker.length())
                            logContent.delete(0, logContent.length() - marker.length());
                        continue;
                    }
                    if (!gotLogs && calcSecFrom(startTime) > LOG_WAIT_LIMIT) {
                        info("Couldn't retrieve logs from " + pod.getName() + " in " + LOG_WAIT_LIMIT + " seconds");
                        return false;
                    }
                    if (calcSecFrom(startTime) > timeoutInSec) {
                        info("Healthcheck timeout " + timeoutInSec + " exceeded for " + pod.getName());
                        return false;
                    }
                    sleepSec(1);
                }
            }
        } catch (Exception e) {
            info("Unexpected error during healthcheck in " + pod.getName() + ": " +
                    e.getClass().getSimpleName() + " " + e.getMessage());
            return false;
        }
    }

    private long calcSecFrom(long startTime) {
        return (currentTimeMillis() - startTime) / 1000;
    }
}
