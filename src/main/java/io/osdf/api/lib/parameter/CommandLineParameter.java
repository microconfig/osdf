package io.osdf.api.lib.parameter;

import io.osdf.api.lib.annotations.ConsoleParam;
import lombok.SneakyThrows;
import org.apache.commons.cli.Option;

public interface CommandLineParameter<T> {
    @SneakyThrows
    static CommandLineParameter<?> createFrom(ConsoleParam consoleParam) {
        return consoleParam.value().getConstructor().newInstance();
    }

    Option toOption();

    String name();

    T get();

    void set(String param);
}
