package io.osdf.api.lib.parameter;

import lombok.Getter;
import org.apache.commons.cli.Option;

public class ArgParameter<T> implements CommandLineParameter<T> {
    private final String name;
    private final Option option;
    @Getter
    private String value;

    public ArgParameter(String longName, String shortName, String description) {
        this.option = new Option(shortName, longName, true, description);
        this.name = longName;
        this.value = null;
    }

    @Override
    public Option toOption() {
        return option;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    @SuppressWarnings("unchecked")
    public T get() {
        return (T) value;
    }

    @Override
    public void set(String param) {
        value = param;
    }
}
