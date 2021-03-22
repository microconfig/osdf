package io.osdf.actions.chaos.events.empty;

import io.osdf.actions.chaos.events.Event;
import io.osdf.actions.chaos.events.EventLevel;
import io.osdf.actions.chaos.events.EventSender;

public class EmptyEventSender implements EventSender {
    public static EmptyEventSender emptyEventSender() {
        return new EmptyEventSender();
    }

    @Override
    public void send() {
        //do nothing
    }

    @Override
    public void send(Event event) {
        //do nothing
    }

    @Override
    public void send(String message, EventLevel level, String... labels) {
        //do nothing
    }

    @Override
    public EventSender newSender(String source) {
        return emptyEventSender();
    }
}
