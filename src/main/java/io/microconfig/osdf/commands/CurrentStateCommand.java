package io.microconfig.osdf.commands;


import lombok.RequiredArgsConstructor;

import java.nio.file.Path;

import static io.microconfig.osdf.state.OSDFState.fromFile;
import static io.microconfig.osdf.state.OSDFStatePrinter.statePrinter;

@RequiredArgsConstructor
public class CurrentStateCommand {
    private final Path stateSavePath;

    public void run() {
        statePrinter(fromFile(stateSavePath)).print();
    }
}
