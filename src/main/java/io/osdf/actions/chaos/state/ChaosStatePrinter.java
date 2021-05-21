package io.osdf.actions.chaos.state;

import io.microconfig.utils.Logger;
import io.osdf.actions.chaos.report.ReportReader;
import io.osdf.common.yaml.YamlObject;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

import static io.microconfig.utils.Logger.announce;
import static io.microconfig.utils.Logger.info;
import static io.osdf.actions.chaos.state.ChaosPhase.FINISHED;
import static io.osdf.actions.chaos.utils.TimeUtils.durationFromString;
import static io.osdf.actions.chaos.utils.TimeUtils.fromTimestamp;
import static io.osdf.core.connection.cli.CliOutput.outputOf;
import static java.nio.file.Files.exists;
import static java.time.Duration.ofSeconds;
import static java.util.stream.Collectors.toUnmodifiableList;

@RequiredArgsConstructor
public class ChaosStatePrinter {
    private final ChaosStateManager chaosStateManager;

    public static ChaosStatePrinter chaosStatePrinter(ChaosStateManager chaosStateManager) {
        return new ChaosStatePrinter(chaosStateManager);
    }

    public void print() {
        if (noChaosState(chaosStateManager)) return;

        ChaosState state = chaosStateManager.state();
        ChaosPhase phase = state.getPhase();
        boolean isPidRunning = outputOf("ps -p " + state.getPid(), 1).ok();
        boolean isFinished = phase == FINISHED;
        LocalDateTime startTime = fromTimestamp(state.getStartTimeMs());

        printTestSummary(state, isPidRunning, isFinished);
        printPhase(phase, isFinished);
        printStartTime(startTime);
        printEndTime(chaosStateManager, isPidRunning, isFinished, startTime);
        printStats(chaosStateManager.reportReader());
        printAssaults(chaosStateManager);
    }

    private void printEndTime(ChaosStateManager chaosStateManager, boolean isPidRunning, boolean isFinished, LocalDateTime startTime) {
        LocalDateTime endTime = endTime(startTime, chaosStateManager,!isFinished && !isPidRunning);
        if (endTime == null) {
            info("End time: unknown");
        } else {
            System.out.printf("End time: %tT %n", endTime);
        }
    }

    private LocalDateTime endTime(LocalDateTime startTime, ChaosStateManager chaosStateManager, boolean crashed) {
        if (crashed) return chaosStateManager.reportReader().lastTime();

        YamlObject scenario = chaosStateManager.scenario();
        int warmupDuration = durationFromString(scenario.get("scenario.warmup"));
        int duration = durationFromString(scenario.get("scenario.duration"));
        return startTime.plus(ofSeconds(warmupDuration))
                .plus(ofSeconds(duration));
    }

    private void printStartTime(LocalDateTime startTime) {
        System.out.printf("Start time: %tT%n", startTime);
    }

    private void printPhase(ChaosPhase phase, boolean isFinished) {
        if (!isFinished) {
            info("Phase: " + phase);
        }
    }

    private void printTestSummary(ChaosState state, boolean isPidRunning, boolean isFinished) {
        String componentName = state.getComponent();
        if (isFinished) {
            announce("Test " + componentName + " is finished");
        } else {
            if (isPidRunning) {
                announce("Test " + componentName + " is live");
            } else {
                announce("Test " + componentName + " crashed");
            }
        }
    }

    private void printStats(ReportReader reportReader) {
        announce("\nStats");
        System.out.printf("Errors: %d%n", reportReader.errorsCount());
    }

    private void printAssaults(ChaosStateManager chaosStateManager) {
        List<String> assaultsDescriptions = chaosStateManager.assaultInfoManager()
                .list().stream()
                .map(info -> info.getAssaultName() + " - " + info.getDescription())
                .collect(toUnmodifiableList());
        if (!assaultsDescriptions.isEmpty()) {
            announce("\nActive assaults");
            assaultsDescriptions.forEach(Logger::info);
        }
    }

    private boolean noChaosState(ChaosStateManager chaosStateManager) {
        if (exists(chaosStateManager.chaosPaths().state())) return false;

        info("Chaos test hasn't been run");
        return true;
    }
}
