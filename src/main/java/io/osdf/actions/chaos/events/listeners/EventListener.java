package io.osdf.actions.chaos.events.listeners;

import io.osdf.actions.chaos.events.Event;

public interface EventListener {
    void process(Event event);
}
