package io.osdf.actions.chaos.runner;

import io.osdf.actions.chaos.assaults.Assault;
import io.osdf.actions.chaos.checks.CheckerResponse;
import io.osdf.actions.chaos.scenario.Scenario;

import static io.microconfig.utils.Logger.info;
import static io.osdf.common.utils.ThreadUtils.calcSecFrom;
import static io.osdf.common.utils.ThreadUtils.sleepSec;
import static java.lang.System.currentTimeMillis;

public class ChaosRunner {
    public static ChaosRunner chaosRunner() {
        return new ChaosRunner();
    }

    public void run(Scenario scenario) {
        info("Starting " + scenario.durationInSec() + "s chaos test");
        info("Starting assaults");
        scenario.assaults().forEach(Assault::start);
        info("Assaults applied");
        long start = currentTimeMillis();
        while (calcSecFrom(start) < scenario.durationInSec()) {
            System.out.println(calcSecFrom(start) / 60 + "min");
            info("Checking cluster health");
            scenario.checkers().forEach(checker -> {
                CheckerResponse response = checker.check();
                info((response.ok() ? "OK" : "FAILED") + "(" + checker.getClass().getSimpleName() + ") - " + response.description());
            });
            info("Checks completed");
            sleepSec(30);
        }
        scenario.assaults().forEach(Assault::stop);
    }
}
