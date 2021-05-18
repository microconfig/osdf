package io.osdf.actions.chaos.runner;

import io.osdf.actions.chaos.ChaosContext;
import io.osdf.actions.chaos.assaults.Assault;
import io.osdf.actions.chaos.checks.Checker;
import io.osdf.actions.chaos.report.ChaosReport;
import io.osdf.actions.chaos.scenario.Scenario;
import io.osdf.core.events.EventDto;
import io.osdf.core.events.EventSender;
import lombok.RequiredArgsConstructor;

import java.util.stream.Collectors;

import static io.osdf.actions.chaos.report.ChaosReport.chaosReport;
import static io.osdf.actions.chaos.state.ChaosPhase.*;
import static io.osdf.actions.chaos.utils.MapperUtils.dump;
import static io.osdf.common.utils.ThreadUtils.calcSecFrom;
import static io.osdf.common.utils.ThreadUtils.sleepSec;
import static io.osdf.core.events.EventLevel.*;
import static java.lang.System.currentTimeMillis;

@RequiredArgsConstructor
public class ChaosRunner {
    private final ChaosContext chaosContext;
    private final EventSender events;

    public static ChaosRunner chaosRunner(ChaosContext chaosContext) {
        return new ChaosRunner(chaosContext, chaosContext.eventStorage().sender("osdf"));
    }

    public void run(Scenario scenario) {
        chaosContext.chaosStateManager().setState(WARMUP);
        events.send("Waiting " + scenario.warmupInSec() / 60 + "m to warm up", CHAOS);
        warmup(scenario);

        chaosContext.chaosStateManager().setState(RUNNING);
        events.send("Starting " + scenario.durationInSec() / 60 + "m chaos test", CHAOS);
        mainPhase(scenario);

        chaosContext.chaosStateManager().setState(FINISHED);
        events.send("Finished chaos test", CHAOS);
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
            saveReport();
            sleepSec(30);
        }
    }

    private void saveReport() {
        long startTime = currentTimeMillis();
        ChaosReport chaosReport = chaosReport(
                chaosContext.eventStorage().events().stream()
                        .map(EventDto::fromEvent)
                        .collect(Collectors.toUnmodifiableList())
        );
        dump(chaosReport, chaosContext.chaosStateManager().chaosPaths().report());
        events.send("Took " + (currentTimeMillis() - startTime) + "ms to save report", DEBUG, "system");
    }
}
