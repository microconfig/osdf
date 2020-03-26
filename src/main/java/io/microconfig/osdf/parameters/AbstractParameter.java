package io.microconfig.osdf.parameters;

import lombok.Getter;
import org.apache.commons.cli.Option;

public class AbstractParameter<T> implements CommandLineParameter<T> {
    private final String key;
    private final Option option;
    @Getter
    private String value;

    private AbstractParameter(String key, String opt, String longOpt, String description) {
        this.option = new Option(opt, longOpt, true, description);
        this.key = key;
        this.value = null;
    }

    public AbstractParameter(String key, String opt, String description) {
        this(key, opt, key, description);
    }

    @Override
    public Option toOption() {
        return option;
    }

    @Override
    public String name() {
        return key;
    }

    @Override
    @SuppressWarnings("unchecked")
    public T get() {
        return (T) value;
    }

    @Override
    public void setString(String param) {
        value = param;
    }

    @Override
    public String missingHint() {
        return "Missing " + key + ". Use osdf init -" + option.getOpt() + " <" + key + "> (" + option.getDescription() + ")";
    }
}
