package io.osdf.core.events.listeners;

import io.osdf.core.events.Event;

public interface EventListener {
    void process(Event event);
}
