package io.microconfig.osdf.configs;

import io.microconfig.osdf.settings.VerifiableFile;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConfigsSettings implements VerifiableFile {
    private ConfigsSource configsSource;
    private String env;
    private String projectVersion;
    private String group;

    @Override
    public boolean verify() {
        return configsSource != null && env != null;
    }
}
