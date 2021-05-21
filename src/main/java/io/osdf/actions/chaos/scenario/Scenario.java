package io.osdf.actions.chaos.scenario;

import io.osdf.actions.chaos.assaults.Assault;
import io.osdf.actions.chaos.checks.Checker;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;

@Getter
@Accessors(fluent = true)
@RequiredArgsConstructor
public class Scenario {
    private final int durationInSec;
    private final int warmupInSec;
    private final List<Assault> assaults;
    private final List<Checker> checkers;
}
