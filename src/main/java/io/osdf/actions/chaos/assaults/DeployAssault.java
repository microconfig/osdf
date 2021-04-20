package io.osdf.actions.chaos.assaults;

import io.osdf.actions.chaos.ChaosContext;
import io.osdf.actions.chaos.events.EventSender;
import io.osdf.actions.management.deploy.AppsDeployCommand;
import lombok.RequiredArgsConstructor;

import static io.osdf.actions.chaos.events.EventLevel.CHAOS;
import static io.osdf.actions.chaos.events.EventLevel.ERROR;
import static io.osdf.actions.management.deploy.AppsDeployCommand.deployCommand;
import static java.lang.Thread.currentThread;

@RequiredArgsConstructor
public class DeployAssault implements Assault {
    private final AppsDeployCommand deployCommand;
    private final EventSender events;

    private volatile Thread thread = null;

    public static DeployAssault deployAssault(Object description, ChaosContext chaosContext) {
        return new DeployAssault(deployCommand(chaosContext.paths(), chaosContext.cli()), chaosContext.eventStorage().sender("deploy assault"));
    }

    @Override
    public void start() {
        events.send("Start deploy", CHAOS);
        thread = new Thread(runnable());
        thread.start();
    }

    private Runnable runnable() {
        return () -> {
            boolean ok = deployCommand.deploy(null, false);
            if (ok) {
                events.send("Finished deploy", CHAOS);
            } else {
                events.send("Deploy failed", ERROR);
            }
        };
    }

    @Override
    public void stop() {
        try {
            thread.join();
        } catch (InterruptedException e) {
            currentThread().interrupt();
        }
    }
}
