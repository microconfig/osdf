package io.osdf.actions.chaos.events;

import io.osdf.actions.chaos.events.listeners.EventListener;

import java.util.List;

public interface EventStorage {
    List<Event> events();

    void save(Event event);

    EventSender sender(String source);

    EventStorage with(EventListener listener);
}
