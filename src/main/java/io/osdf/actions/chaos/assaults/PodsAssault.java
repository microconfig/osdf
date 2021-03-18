package io.osdf.actions.chaos.assaults;

import io.osdf.core.application.service.ServiceApplication;
import io.osdf.core.cluster.pod.Pod;
import io.osdf.core.connection.cli.ClusterCli;
import io.osdf.settings.paths.OsdfPaths;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.Random;

import static io.microconfig.utils.Logger.info;
import static io.osdf.actions.chaos.utils.TimeUtils.durationFromString;
import static io.osdf.common.utils.ThreadUtils.sleepSec;
import static io.osdf.core.application.core.files.loaders.ApplicationFilesLoaderImpl.activeRequiredAppsLoader;
import static io.osdf.core.application.service.ServiceApplicationMapper.service;
import static java.lang.Math.floorMod;
import static java.lang.Thread.currentThread;

@RequiredArgsConstructor
public class PodsAssault implements Assault {
    private final List<ServiceApplication> apps;
    private final int periodInSec;

    private volatile Thread thread = null;
    private volatile boolean stopped = false;

    @SuppressWarnings("unchecked")
    public static PodsAssault podsAssault(Object description, ClusterCli cli, OsdfPaths paths) {
        List<ServiceApplication> apps = activeRequiredAppsLoader(paths, null).load(service(cli));
        Map<String, String> assault = (Map<String, String>) ((List<Object>) description).get(0);
        return new PodsAssault(apps, durationFromString(assault.get("period")));
    }

    @Override
    public void start() {
        thread = new Thread(runnable());
        thread.start();
        info("Started pods assault");
    }

    @Override
    public void stop() {
        stopped = true;
        try {
            thread.join();
        } catch (InterruptedException e) {
            currentThread().interrupt();
        }
        info("Stopped pods assault");
    }

    private Runnable runnable() {
        Random random = new Random();
        return () -> {
            while (!stopped) {
                int appInd = random.nextInt(apps.size());
                apps.get(appInd).deployment().ifPresent(deployment -> {
                    List<Pod> pods = deployment.pods();
                    if (pods.size() == 0) {
                        info("No pods found for deployment " + deployment.name());
                        return;
                    }
                    int podInd = floorMod(random.nextInt(), pods.size());
                    pods.get(podInd).delete();
                    info("Deleted pod " + pods.get(podInd).getName());
                });
                sleepSec(periodInSec);
            }
        };
    }
}
