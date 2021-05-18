package io.osdf.actions.chaos.report;

import io.osdf.actions.chaos.state.ChaosStateManager;
import io.osdf.core.events.EventDto;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

import static io.osdf.actions.chaos.report.ChaosReport.emptyReport;
import static io.osdf.actions.chaos.utils.MapperUtils.createFromPath;
import static io.osdf.actions.chaos.utils.TimeUtils.fromTimestamp;
import static java.nio.file.Files.exists;

@RequiredArgsConstructor
public class ReportReader {
    private final ChaosReport report;

    public static ReportReader reportReader(ChaosStateManager chaosStateManager) {
        if (!exists(chaosStateManager.chaosPaths().report())) return new ReportReader(emptyReport());
        ChaosReport report = createFromPath(chaosStateManager.chaosPaths().report(), ChaosReport.class);
        return new ReportReader(report);
    }

    public long errorsCount() {
        return report
                .getEvents().stream()
                .filter(event -> event.getLevel().equalsIgnoreCase("error"))
                .count();
    }

    public LocalDateTime lastTime() {
        List<EventDto> events = report.getEvents();
        if (events.isEmpty()) return null;

        return fromTimestamp(events.get(events.size() - 1).getTimestampMs());
    }
}
