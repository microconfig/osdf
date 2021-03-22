package io.osdf.actions.chaos.runner;

import io.osdf.actions.chaos.assaults.Assault;
import io.osdf.actions.chaos.checks.Checker;
import io.osdf.actions.chaos.events.EventSender;
import io.osdf.actions.chaos.events.EventStorage;
import io.osdf.actions.chaos.scenario.Scenario;
import lombok.RequiredArgsConstructor;

import static io.osdf.actions.chaos.events.EventLevel.CHAOS;
import static io.osdf.actions.chaos.events.EventLevel.INFO;
import static io.osdf.actions.chaos.events.EventStorageImpl.eventStorage;
import static io.osdf.actions.chaos.events.listeners.LoggerEventListener.logger;
import static io.osdf.common.utils.ThreadUtils.calcSecFrom;
import static io.osdf.common.utils.ThreadUtils.sleepSec;
import static java.lang.System.currentTimeMillis;

@RequiredArgsConstructor
public class ChaosRunner {
    private final EventStorage storage;
    private final EventSender events;

    public static ChaosRunner chaosRunner() {
        EventStorage storage = eventStorage().with(logger());
        return new ChaosRunner(storage, storage.sender("osdf"));
    }

    public void run(Scenario scenario) {
        events.send("Waiting " + scenario.warmupInSec() / 60 + "m to warm up", CHAOS);
        warmup(scenario);
        events.send("Starting " + scenario.durationInSec() / 60 + "m chaos test", CHAOS);
        mainPhase(scenario);
    }

    private void warmup(Scenario scenario) {
        checkHealthFor(scenario.warmupInSec(), scenario);
    }

    private void mainPhase(Scenario scenario) {
        scenario.assaults().forEach(Assault::start);
        checkHealthFor(scenario.durationInSec(), scenario);
        scenario.assaults().forEach(Assault::stop);
    }

    private void checkHealthFor(int durationInSec, Scenario scenario) {
        long start = currentTimeMillis();
        while (calcSecFrom(start) < durationInSec) {
            events.send("Checking cluster health", INFO);
            scenario.checkers().forEach(Checker::check);
            events.send("Checks completed", INFO);
            sleepSec(30);
        }
    }

}
