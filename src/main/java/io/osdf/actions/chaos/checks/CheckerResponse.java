package io.osdf.actions.chaos.checks;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
@RequiredArgsConstructor
public class CheckerResponse {
    private final boolean ok;
    private final String description;
}
