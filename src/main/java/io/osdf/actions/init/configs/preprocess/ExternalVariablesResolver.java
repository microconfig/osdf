package io.osdf.actions.init.configs.preprocess;

import io.osdf.common.exceptions.OSDFException;
import io.osdf.common.exceptions.PossibleBugException;
import io.osdf.common.yaml.YamlObject;
import io.osdf.core.local.configs.ConfigsSettings;
import io.osdf.settings.paths.OsdfPaths;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

import static io.osdf.common.SettingsFile.settingsFile;
import static io.osdf.common.utils.FileUtils.readAll;
import static io.osdf.common.utils.FileUtils.writeStringToFile;
import static io.osdf.common.yaml.YamlObject.yaml;
import static java.nio.file.Files.exists;
import static java.nio.file.Files.walk;

@RequiredArgsConstructor
public class ExternalVariablesResolver {
    private final YamlObject variables;

    public static ExternalVariablesResolver externalVariablesResolver(OsdfPaths paths) {
        String externalPath = settingsFile(ConfigsSettings.class, paths.settings().configs()).getSettings().getExternalPath();
        if (externalPath == null || !exists(Path.of(externalPath))) return new ExternalVariablesResolver(yaml(Map.of()));

        return new ExternalVariablesResolver(yaml(Path.of(externalPath)));
    }

    public void resolve(Path path) {
        try (Stream<Path> walk = walk(path)) {
            walk.filter(Files::isRegularFile).forEach(this::injectVariables);
        } catch (IOException e) {
            throw new PossibleBugException("Can't list files at " + path, e);
        }
    }

    private void injectVariables(Path file) {
        String content = readAll(file);
        while (true) {
            int ind = content.indexOf("~external(");
            if (ind == -1) break;
            int startInd = content.indexOf("(", ind);
            int commaInd = content.indexOf(",", ind);
            int endInd = content.indexOf(")", ind);
            String value = getValue(content, startInd, commaInd, endInd);
            content = content.replace(content.substring(ind, endInd + 1), value);
        }
        writeStringToFile(file, content);
    }

    private String getValue(String content, int startInd, int commaInd, int endInd) {
        String key = content.substring(startInd + 1, commaInd == -1 ? endInd : commaInd).trim();
        String defaultValue = commaInd != -1 ? content.substring(commaInd + 1, endInd).trim() : null;
        return Stream.of(variables.get(key), defaultValue)
                .filter(Objects::nonNull)
                .findFirst()
                .orElseThrow(() -> new OSDFException("External variable required for " + key));
    }
}
