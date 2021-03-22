package io.osdf.actions.chaos.events;

import io.osdf.actions.chaos.events.listeners.EventListener;

import java.util.ArrayList;
import java.util.List;

import static io.osdf.actions.chaos.events.EventSenderImpl.eventSender;

public class EventStorageImpl implements EventStorage {
    private final List<Event> events = new ArrayList<>();
    private final List<EventListener> listeners = new ArrayList<>();

    public static EventStorageImpl eventStorage() {
        return new EventStorageImpl();
    }

    @Override
    public List<Event> events() {
        return events;
    }

    @Override
    public synchronized void save(Event event) {
        listeners.forEach(listener -> listener.process(event));
        events.add(event);
    }

    @Override
    public EventSender sender(String source) {
        return eventSender(this, source);
    }

    @Override
    public EventStorage with(EventListener listener) {
        listeners.add(listener);
        return this;
    }
}
