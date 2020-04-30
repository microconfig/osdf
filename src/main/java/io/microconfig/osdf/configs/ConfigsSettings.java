package io.microconfig.osdf.configs;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConfigsSettings {
    private ConfigsSource configsSource;
    private String env;
    private String projectVersion;
    private String group;

    public boolean verify() {
        return configsSource != null && env != null;
    }
}
