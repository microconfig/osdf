package io.osdf.actions.chaos.state;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ChaosState {
    private ChaosPhase phase;
    private long startTimeMs;
    private String component;
    private long pid;
}
