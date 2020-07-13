package io.osdf.core.local.configs;

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

    @Override
    public String toString() {
        return "" +
                (env == null ? "" :
                        "Env: " + env + "\n") +
                (projectVersion == null ? "" :
                        "Project version: " + projectVersion + "\n") +
                (group == null ? "" :
                        "Group: " + group + "\n");
    }
}
