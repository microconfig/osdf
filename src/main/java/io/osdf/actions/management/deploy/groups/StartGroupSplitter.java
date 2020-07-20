package io.osdf.actions.management.deploy.groups;

import io.osdf.core.application.core.Application;

import java.util.List;
import java.util.TreeMap;

import static java.util.Objects.requireNonNullElse;
import static java.util.stream.Collectors.*;

public class StartGroupSplitter {
    public static StartGroupSplitter startGroupSplitter() {
        return new StartGroupSplitter();
    }

    public List<List<Application>> split(List<Application> apps) {
        return apps.stream()
                .collect(groupingBy(this::startGroup, TreeMap::new, toList()))
                .values().stream()
                .collect(toUnmodifiableList());
    }

    private int startGroup(Application app) {
        Integer startGroup = app.files().deployProperties().get("osdf.startGroup");
        return requireNonNullElse(startGroup, 100);
    }
}
