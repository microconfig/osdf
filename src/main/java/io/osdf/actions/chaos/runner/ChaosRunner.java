package io.osdf.actions.chaos.runner;

import io.osdf.actions.chaos.assaults.Assault;
import io.osdf.actions.chaos.checks.CheckerResponse;
import io.osdf.actions.chaos.scenario.Scenario;
import lombok.extern.slf4j.Slf4j;

import static io.microconfig.utils.Logger.info;
import static io.osdf.common.utils.ThreadUtils.calcSecFrom;
import static io.osdf.common.utils.ThreadUtils.sleepSec;
import static java.lang.System.currentTimeMillis;

@Slf4j
public class ChaosRunner {
    private boolean assaultsStarted = false;

    public static ChaosRunner chaosRunner() {
        return new ChaosRunner();
    }

    public void run(Scenario scenario) {
        log.info("Starting " + scenario.durationInSec() / 60 + "s chaos test");
        long start = currentTimeMillis();
        while (calcSecFrom(start) < scenario.durationInSec()) {
            if (calcSecFrom(start) > scenario.warmupInSec() && !assaultsStarted) {
                log.info("Starting assaults");
                scenario.assaults().forEach(Assault::start);
                log.info("Assaults applied");
                assaultsStarted = true;
            }
            info("");
            log.info("Checking cluster health");
            scenario.checkers().forEach(checker -> {
                CheckerResponse response = checker.check();
                log.info((response.ok() ? "OK" : "FAILED") + "(" + checker.getClass().getSimpleName() + ") - " + response.description());
            });
            log.info("Checks completed");
            sleepSec(30);
        }
        scenario.assaults().forEach(Assault::stop);
    }
}
