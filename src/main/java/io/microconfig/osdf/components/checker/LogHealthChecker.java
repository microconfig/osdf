package io.microconfig.osdf.components.checker;

import io.microconfig.osdf.microconfig.properties.HealthCheckProperties;
import io.microconfig.osdf.openshift.Pod;
import lombok.RequiredArgsConstructor;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import static io.microconfig.osdf.utils.ThreadUtils.sleepSec;
import static io.microconfig.utils.TimeUtils.calcSecFrom;
import static java.lang.Runtime.getRuntime;
import static java.lang.System.currentTimeMillis;

@RequiredArgsConstructor
public class LogHealthChecker implements HealthChecker {
    private final HealthCheckProperties properties;

    public static LogHealthChecker logHealthChecker(HealthCheckProperties properties) {
        return new LogHealthChecker(properties);
    }

    public boolean check(Pod pod) {
        String marker = properties.marker(pod.getComponentName());
        int timeoutInSec = properties.timeoutInSec(pod.getComponentName());
        try {
            Process process = getRuntime().exec("oc logs -f " + pod.getName() + " -c " + pod.getComponentName());
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                long startTime = currentTimeMillis();
                StringBuilder logContent = new StringBuilder();
                while (true) {
                    if (reader.ready()) {
                        logContent.append(reader.readLine());
                        if (logContent.indexOf(marker) >= 0) return true;
                        if (logContent.length() > marker.length())
                            logContent.delete(0, logContent.length() - marker.length());
                        continue;
                    }
                    if (calcSecFrom(startTime) > timeoutInSec) return false;
                    sleepSec(1);
                }
            }
        } catch (Exception e) {
            return false;
        }
    }
}
