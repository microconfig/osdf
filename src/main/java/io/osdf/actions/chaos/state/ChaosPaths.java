package io.osdf.actions.chaos.state;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

import java.nio.file.Path;

import static java.nio.file.Path.of;

@RequiredArgsConstructor
public class ChaosPaths {
    @Getter
    @Accessors(fluent = true)
    private final Path root;

    public Path state() {
        return of(root + "/state.yaml");
    }

    public Path scenario() {
        return of(root + "/scenario.yaml");
    }

    public Path report() {
        return of(root + "/report.json");
    }

    public Path assaultInfo() {
        return of(root + "/assault-info");
    }

    public Path log() {
        return of(root + "/chaos.log");
    }
}