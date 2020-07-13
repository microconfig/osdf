package io.osdf.api.lib.parameter;

import org.apache.commons.cli.Option;

public interface CommandLineParameter<T> {
    Option toOption();

    String name();

    T get();

    void set(String param);
}
