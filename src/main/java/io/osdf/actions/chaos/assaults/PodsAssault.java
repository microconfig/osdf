package io.osdf.actions.chaos.assaults;

import io.osdf.actions.chaos.ChaosContext;
import io.osdf.actions.chaos.events.EventSender;
import io.osdf.common.yaml.YamlObject;
import io.osdf.core.application.service.ServiceApplication;
import io.osdf.core.cluster.pod.Pod;
import io.osdf.core.connection.cli.ClusterCli;
import io.osdf.settings.paths.OsdfPaths;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Random;

import static io.osdf.actions.chaos.events.EventLevel.CHAOS;
import static io.osdf.actions.chaos.events.empty.EmptyEventSender.emptyEventSender;
import static io.osdf.actions.chaos.utils.TimeUtils.durationFromString;
import static io.osdf.common.utils.ThreadUtils.sleepSec;
import static io.osdf.common.yaml.YamlObject.yaml;
import static io.osdf.core.application.core.files.loaders.ApplicationFilesLoaderImpl.activeRequiredAppsLoader;
import static io.osdf.core.application.service.ServiceApplicationMapper.service;
import static java.lang.Math.floorMod;
import static java.lang.Thread.currentThread;

@RequiredArgsConstructor
public class PodsAssault implements Assault {
    private final List<ServiceApplication> apps;
    private final int periodInSec;

    private final EventSender events;

    private volatile Thread thread = null;
    private volatile boolean stopped = false;

    @SuppressWarnings("unchecked")
    public static PodsAssault podsAssault(Object description, ChaosContext chaosContext) {
        YamlObject assault = yaml(((List<Object>) description).get(0));
        List<ServiceApplication> apps = activeRequiredAppsLoader(chaosContext.paths(), assault.get("services"))
                .load(service(chaosContext.cli()));
        return new PodsAssault(apps, durationFromString(assault.get("period")), chaosContext.eventStorage().sender("pods assault"));
    }

    @Override
    public void start() {
        thread = new Thread(runnable());
        thread.start();
        events.send("Started pods assault", CHAOS);
    }

    @Override
    public void stop() {
        stopped = true;
        try {
            thread.join();
        } catch (InterruptedException e) {
            currentThread().interrupt();
        }
        events.send("Stopped pods assault", CHAOS);
    }

    private Runnable runnable() {
        Random random = new Random();
        return () -> {
            while (!stopped) {
                int appInd = random.nextInt(apps.size());
                apps.get(appInd).deployment().ifPresent(deployment -> {
                    List<Pod> pods = deployment.pods();
                    if (pods.size() == 0) {
                        events.send("No pods found for deployment " + deployment.name(), CHAOS, deployment.name());
                        return;
                    }
                    int podInd = floorMod(random.nextInt(), pods.size());
                    pods.get(podInd).delete();
                    events.send("Deleted pod " + pods.get(podInd).getName(), CHAOS, deployment.name());
                });
                sleepSec(periodInSec);
            }
        };
    }
}
