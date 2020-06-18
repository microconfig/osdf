package io.microconfig.osdf.service.deployment.checkers.healthcheck;

import io.cluster.old.cluster.pod.Pod;
import lombok.RequiredArgsConstructor;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import static io.microconfig.osdf.utils.ThreadUtils.sleepSec;
import static io.microconfig.utils.Logger.warn;
import static java.lang.Runtime.getRuntime;
import static java.lang.System.currentTimeMillis;

@RequiredArgsConstructor
public class LogHealthChecker implements HealthChecker {
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
                        if (numLines > 500) {
                            warn("Line limit exceeded for " + pod.getName() + " in log healthcheck.");
                            return true;
                        }
                        logContent.append(str);
                        if (logContent.indexOf(marker) >= 0) return true;
                        if (logContent.length() > marker.length())
                            logContent.delete(0, logContent.length() - marker.length());
                        continue;
                    }
                    if (!gotLogs && calcSecFrom(startTime) > 10) return false;
                    if (calcSecFrom(startTime) > timeoutInSec) return false;
                    sleepSec(1);
                }
            }
        } catch (Exception e) {
            return false;
        }
    }

    private long calcSecFrom(long startTime) {
        return (currentTimeMillis() - startTime) / 1000;
    }
}
