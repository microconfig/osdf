package io.microconfig.osdf.utils;

import io.microconfig.osdf.exceptions.PossibleBugException;
import io.microconfig.osdf.exceptions.ServerNotAvailableException;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import static java.nio.charset.StandardCharsets.UTF_8;

public class HTTPUtils {
    public static String get(String url) {
        try {
            HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
            con.setRequestMethod("GET");
            if (con.getResponseCode() == 503 || con.getResponseCode() == 502) {
                throw new ServerNotAvailableException("Server not available on " + url);
            }
            if (con.getResponseCode() != 200) {
                throw new PossibleBugException(url + " returned " + con.getResponseCode() + " status code");
            }
            return IOUtils.toString(con.getInputStream(), UTF_8.name());
        } catch (IOException e) {
            throw new PossibleBugException("Error querying " + url, e);
        }
    }
}
