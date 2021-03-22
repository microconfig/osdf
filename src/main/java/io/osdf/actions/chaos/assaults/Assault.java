package io.osdf.actions.chaos.assaults;

import io.osdf.actions.chaos.events.EventSource;

public interface Assault extends EventSource {
    void start();

    void stop();
}
