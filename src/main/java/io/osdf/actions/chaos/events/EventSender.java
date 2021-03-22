package io.osdf.actions.chaos.events;

public interface EventSender {
    void send();

    void send(Event event);

    void send(String message, EventLevel level, String... labels);

    EventSender newSender(String source);
}
