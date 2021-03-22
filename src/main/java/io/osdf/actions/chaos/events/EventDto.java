package io.osdf.actions.chaos.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventDto {
    private long timestampMs;
    private String source;
    private String level;
    private List<String> labels;
    private String message;
}
