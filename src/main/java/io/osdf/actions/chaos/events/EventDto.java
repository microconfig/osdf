package io.osdf.actions.chaos.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.List;

import static java.time.ZoneId.systemDefault;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventDto {
    private long timestampMs;
    private String source;
    private String level;
    private List<String> labels;
    private String message;

    public static EventDto fromEvent(Event event) {
        return new EventDto(
                event.time().atZone(systemDefault()).toInstant().toEpochMilli(),
                event.source(),
                event.level().toString(),
                event.labels(),
                event.message()
        );
    }
}
