package io.osdf.actions.chaos.events;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static io.osdf.actions.chaos.events.EventLevel.INFO;
import static io.osdf.actions.chaos.events.EventLevel.valueOf;
import static io.osdf.actions.chaos.utils.TimeUtils.fromTimestamp;
import static java.time.LocalDateTime.now;

@Data
@Accessors(fluent = true)
public class Event {
    private LocalDateTime time;
    private String source;
    private EventLevel level;
    private List<String> labels;
    private String message;

    public static Event newEvent() {
        return new Event()
                .time(now())
                .source("-")
                .level(INFO)
                .labels(new ArrayList<>())
                .message("");
    }

    public static Event event(EventDto eventDto) {
        return new Event()
                .time(fromTimestamp(eventDto.getTimestampMs()))
                .source(eventDto.getSource())
                .level(valueOf(eventDto.getLevel()))
                .labels(eventDto.getLabels())
                .message(eventDto.getMessage());
    }
}