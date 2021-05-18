package io.osdf.core.events;

public interface EventSender {
    void send();

    void send(Event event);

    void send(String message, EventLevel level, String... labels);
}
