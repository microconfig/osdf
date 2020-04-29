package io.microconfig.osdf.api;

import io.microconfig.osdf.api.argsproducer.ConsoleArgs;
import io.microconfig.osdf.paths.OSDFPaths;
import org.junit.jupiter.api.Test;

import static io.microconfig.osdf.api.ApiCaller.apiCaller;
import static io.microconfig.osdf.api.OSDFApiImpl.osdfApi;
import static java.nio.file.Path.of;

class ApiCallerTest {
    @Test
    void call() {
        OSDFPaths paths = new OSDFPaths(of("/tmp/path"));
        apiCaller(ConsoleArgs.consoleArgs(new String[]{"help"})).callCommand(osdfApi(paths, null), "help");
    }

    @Test
    void callUnknown() {
        OSDFPaths paths = new OSDFPaths(of("/tmp/path"));
        try {
            apiCaller(ConsoleArgs.consoleArgs(new String[]{})).callCommand(osdfApi(paths, null), "unknownMethod");
        } catch (Exception ignored) {
            return;
        }
        throw new RuntimeException("Call of unknown method should throw exception");
    }
}