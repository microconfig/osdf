package io.osdf.actions.chaos.checks;

import io.osdf.actions.chaos.events.EventSource;

public interface Checker extends EventSource {
    CheckerResponse check();
}
