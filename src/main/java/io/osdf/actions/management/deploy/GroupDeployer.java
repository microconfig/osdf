package io.osdf.actions.management.deploy;

import io.osdf.actions.management.deploy.deployer.AppHealth;
import io.osdf.actions.management.deploy.deployer.Deployable;
import io.osdf.core.application.core.Application;
import io.osdf.core.connection.cli.ClusterCli;
import io.osdf.core.events.EventLevel;
import io.osdf.core.events.EventSender;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

import static io.osdf.actions.management.deploy.deployer.AppHealth.OK;
import static io.osdf.actions.management.deploy.deployer.AppHealth.TIMEOUT;
import static io.osdf.actions.management.deploy.deployer.Deployable.of;
import static io.osdf.common.utils.ThreadUtils.runInParallel;
import static io.osdf.core.events.EventLevel.INFO;
import static java.lang.System.currentTimeMillis;
import static java.lang.System.getenv;
import static java.util.stream.Collectors.toUnmodifiableList;

@RequiredArgsConstructor
public class GroupDeployer {
    private final ClusterCli cli;
    private final Integer maxParallel;
    private final boolean waitIfTimeout;
    private final EventSender events;
    private final EventLevel level;

    public static GroupDeployer groupDeployer(ClusterCli cli, Integer maxParallel, EventSender events, EventLevel level) {
        return new GroupDeployer(cli, maxParallel, !"false".equals(getenv("OSDF_DEPLOY_FAILFAST")), events, level);
    }

    public boolean deployGroup(List<Application> apps) {
        events.send("Deploying group - " + apps.stream().map(Application::name).collect(toUnmodifiableList()), level);
        Queue<Deployable> timedOutApps = new ArrayBlockingQueue<>(apps.size());
        Boolean result = runInParallel(parallelism(apps), () -> apps.parallelStream()
                .map(app -> of(app, cli))
                .allMatch(app -> deployAndReturnStatus(app, timedOutApps) != AppHealth.ERROR)
        );
        if (!waitIfTimeout || timedOutApps.isEmpty()) return result;
        events.send("Checking health of timed out apps", INFO);
        return timedOutApps.stream()
                .allMatch(app -> checkHealth(app) == OK);
    }

    private AppHealth deployAndReturnStatus(Deployable app, Queue<Deployable> timedOutApps) {
        events.send("Deploying " + app.name(), level, app.name());
        boolean isSuccessful = app.deploy();
        if (!isSuccessful) {
            events.send(app.name() + " has failed", EventLevel.ERROR, app.name());
            return AppHealth.ERROR;
        }
        AppHealth result = checkHealth(app);
        if (result == TIMEOUT) {
            timedOutApps.add(app);
        }
        return result;
    }

    private AppHealth checkHealth(Deployable app) {
        long start = currentTimeMillis();
        AppHealth check = app.check();
        long timeMs = currentTimeMillis() - start;
        switch (check) {
            case ERROR:
                events.send(app.name() + " has failed", EventLevel.ERROR, app.name());
                break;
            case TIMEOUT:
                events.send(app.name() + " has timed out (" + timeMs/1000 + "s)", level, app.name());
                break;
            case OK:
                events.send(app.name() + " was deployed successfully (" + timeMs / 1000 + "s)", level, app.name());
                break;
        }
        return check;
    }

    private int parallelism(List<Application> apps) {
        return maxParallel == null ? apps.size() : maxParallel;
    }
}
