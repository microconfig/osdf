package io.osdf.actions.chaos.assaults;

import io.osdf.actions.chaos.ChaosContext;
import io.osdf.actions.management.deploy.AppsDeployCommand;
import io.osdf.core.events.EventSender;
import lombok.RequiredArgsConstructor;

import static io.osdf.actions.management.deploy.AppsDeployCommand.deployCommand;
import static io.osdf.core.events.EventLevel.CHAOS;
import static io.osdf.core.events.EventLevel.ERROR;
import static java.lang.Thread.currentThread;

@RequiredArgsConstructor
public class DeployAssault implements Assault {
    private final AppsDeployCommand deployCommand;
    private final EventSender events;

    private volatile Thread thread = null;

    public static DeployAssault deployAssault(Object description, ChaosContext chaosContext) {
        EventSender eventSender = chaosContext.eventStorage().sender("deploy assault");
        return new DeployAssault(deployCommand(chaosContext.paths(), chaosContext.cli(), eventSender, CHAOS), eventSender);
    }

    @Override
    public void start() {
        events.send("Start deploy", CHAOS);
        thread = new Thread(runnable());
        thread.start();
    }

    private Runnable runnable() {
        return () -> {
            boolean ok = deployCommand.deploy(null, false, "chaos");
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
