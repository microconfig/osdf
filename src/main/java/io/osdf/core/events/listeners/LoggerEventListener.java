package io.osdf.core.events.listeners;

import io.osdf.actions.chaos.state.ChaosPaths;
import io.osdf.common.exceptions.PossibleBugException;
import io.osdf.core.events.Event;
import io.osdf.core.events.EventLevel;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import static io.osdf.core.events.listeners.ConsoleColors.colorize;
import static java.lang.Math.floorMod;
import static java.lang.String.format;
import static java.nio.file.Files.write;
import static java.nio.file.StandardOpenOption.APPEND;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.util.List.of;
import static java.util.Objects.isNull;
import static java.util.stream.Collectors.joining;

@RequiredArgsConstructor
public class LoggerEventListener implements EventListener {
    private final EventLevel logLevel;
    private final Path logFile;

    public static LoggerEventListener logger(EventLevel logLevel, ChaosPaths paths) {
        return new LoggerEventListener(logLevel, paths.log());
    }

    public static LoggerEventListener logger(EventLevel logLevel) {
        return new LoggerEventListener(logLevel, null);
    }

    @Override
    public synchronized void process(Event event) {
        if (event.level().compareTo(logLevel) < 0) return;

        System.out.print(logString(event, true));
        writeToLogFile(event);
    }

    private void writeToLogFile(Event event) {
        if (isNull(logFile)) return;
        try {
            write(logFile, logString(event, false).getBytes(), CREATE, APPEND);
        } catch (IOException e) {
            throw new PossibleBugException("Can't append to log file " + logFile, e);
        }
    }

    private String logString(Event event, boolean withColor) {
        return format("%tT [%s %s%s] - %s%n",
                event.time(),
                colorize(event.level().toString(), withColor),
                colorize(event.source(), withColor),
                labelsString(event.labels(), withColor),
                event.message());
    }

    private String labelsString(List<String> labels, boolean withColor) {
        if (labels.isEmpty()) return "";
        return "{" + labels.stream().map(label -> colorize(label, withColor)).collect(joining(",")) + "}";
    }
}

class ConsoleColors {
    public static final String RESET = "\033[0m";  // Text Reset

    public static final String RED = "\033[0;31m";
    public static final String GREEN = "\033[0;32m";
    public static final String YELLOW = "\033[0;33m";
    public static final String BLUE = "\033[0;34m";
    public static final String PURPLE = "\033[0;35m";
    public static final String CYAN = "\033[0;36m";

    public static final String RED_BOLD = "\033[1;31m";
    public static final String GREEN_BOLD = "\033[1;32m";
    public static final String YELLOW_BOLD = "\033[1;33m";
    public static final String BLUE_BOLD = "\033[1;34m";
    public static final String PURPLE_BOLD = "\033[1;35m";
    public static final String CYAN_BOLD = "\033[1;36m";

    public static final List<String> colors = of(RED, GREEN, YELLOW, BLUE, PURPLE, CYAN,
            RED_BOLD, GREEN_BOLD, YELLOW_BOLD, BLUE_BOLD, PURPLE_BOLD, CYAN_BOLD);

    public static String colorize(String str, boolean apply) {
        if ("error".equalsIgnoreCase(str)) return RED_BOLD;
        if (!apply) return str;
        int ind = floorMod(str.hashCode(), colors.size());
        return colors.get(ind) + str + RESET;
    }
}
