package io.osdf.actions.chaos.report;

import io.osdf.core.events.EventDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

import static java.util.Collections.emptyList;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChaosReport {
    private List<EventDto> events;

    public static ChaosReport chaosReport(List<EventDto> events) {
        return new ChaosReport(events);
    }

    public static ChaosReport emptyReport() {
        return new ChaosReport(emptyList());
    }
}
