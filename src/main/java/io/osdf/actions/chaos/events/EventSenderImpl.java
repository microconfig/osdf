package io.osdf.actions.chaos.events;

import lombok.RequiredArgsConstructor;

import static io.osdf.actions.chaos.events.Event.newEvent;
import static java.util.Arrays.asList;

@RequiredArgsConstructor
public class EventSenderImpl implements EventSender {
    private final EventStorage storage;
    private final String source;
    private Event event = newEvent();

    public static EventSenderImpl eventSender(EventStorage storage, String source) {
        EventSenderImpl eventSender = new EventSenderImpl(storage, source);
        eventSender.event.source(source);
        return eventSender;
    }

    @Override
    public void send() {
        storage.save(event);
        event = newEvent().source(source);
    }

    @Override
    public void send(Event event) {
        storage.save(event);
    }

    @Override
    public void send(String message, EventLevel level, String... labels) {
        event.message(message)
                .level(level)
                .labels(asList(labels));
        send();
    }

    @Override
    public EventSender newSender(String source) {
        return eventSender(storage, source);
    }
}
