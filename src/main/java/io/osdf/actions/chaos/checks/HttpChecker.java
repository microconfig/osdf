package io.osdf.actions.chaos.checks;

import lombok.RequiredArgsConstructor;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;

@RequiredArgsConstructor
public class HttpChecker implements Checker {
    private final String url;

    public static HttpChecker httpChecker(Map<String, Object> description) {
        return new HttpChecker((String) description.get("url"));
    }

    @Override
    public CheckerResponse check() {
        try {
            return doCheck();
        } catch (IOException e) {
            return new CheckerResponse(false, "Failed to query url " + url + "- " + e.getClass().getSimpleName() + " " + e.getMessage());
        }
    }

    private CheckerResponse doCheck() throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestMethod("GET");
        connection.setConnectTimeout(2000);

        int responseCode = connection.getResponseCode();
        if (responseCode == 200) return new CheckerResponse(true, "OK");
        return new CheckerResponse(false, IOUtils.toString(connection.getErrorStream(), UTF_8.name()));
    }
}
