package io.microconfig.osdf.parameters;

import org.apache.commons.cli.Option;

public class FlagParameter implements CommandLineParameter<Boolean> {
    private final String name;
    private final Option option;
    private Boolean value;

    public FlagParameter(String longName, String shortName, String description) {
        this.option = new Option(shortName, longName, false, description);
        this.name = longName;
        this.value = false;
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
    public Boolean get() {
        return value;
    }

    @Override
    public void set(String param) {
        value = "true".equals(param);
    }
}
