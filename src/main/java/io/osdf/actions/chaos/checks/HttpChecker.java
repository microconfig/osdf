package io.osdf.actions.chaos.checks;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.osdf.actions.chaos.events.Event;
import io.osdf.actions.chaos.events.EventDto;
import io.osdf.actions.chaos.events.EventSender;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

import static io.osdf.actions.chaos.events.EventLevel.ERROR;
import static io.osdf.actions.chaos.events.empty.EmptyEventSender.emptyEventSender;
import static java.nio.charset.StandardCharsets.UTF_8;

@RequiredArgsConstructor
public class HttpChecker implements Checker {
    private final String url;
    private EventSender events = emptyEventSender();

    public static HttpChecker httpChecker(Map<String, Object> description) {
        return new HttpChecker((String) description.get("url"));
    }

    @Override
    public CheckerResponse check() {
        try {
            return doCheck();
        } catch (IOException e) {
            String message = "Failed to query url " + url + "- " + e.getClass().getSimpleName() + " " + e.getMessage();
            events.send(message, ERROR);
            return new CheckerResponse(false, message);
        }
    }

    private CheckerResponse doCheck() throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestMethod("GET");
        connection.setConnectTimeout(2000);

        int responseCode = connection.getResponseCode();
        List<EventDto> eventDto = new ObjectMapper().readValue(body(connection, responseCode), new TypeReference<>() {
        });
        eventDto.stream()
                .map(Event::event)
                .forEach(events::send);
        return new CheckerResponse(responseCode == 200, responseCode == 200 ? "ok" : "failed");
    }

    private String body(HttpURLConnection connection, int responseCode) throws IOException {
        return IOUtils.toString(responseCode == 200 ? connection.getInputStream() : connection.getErrorStream(), UTF_8);
    }

    @Override
    public HttpChecker setEventSender(EventSender sender) {
        events = sender.newSender("http checker");
        return this;
    }
}
