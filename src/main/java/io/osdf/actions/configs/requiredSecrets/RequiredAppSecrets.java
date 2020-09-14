package io.osdf.actions.configs.requiredSecrets;

import io.osdf.common.yaml.YamlObject;
import io.osdf.core.application.core.Application;
import io.osdf.core.application.core.files.metadata.LocalResourceMetadata;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static io.osdf.common.yaml.YamlObject.yaml;
import static java.nio.file.Path.of;
import static java.util.Collections.emptySet;
import static java.util.stream.Collectors.toUnmodifiableList;
import static java.util.stream.Collectors.toUnmodifiableSet;

public class RequiredAppSecrets {
    public static RequiredAppSecrets requiredAppSecrets() {
        return new RequiredAppSecrets();
    }

    public List<String> listFor(List<Application> apps) {
        return apps.stream()
                .map(this::appSecrets)
                .flatMap(Collection::stream)
                .collect(toUnmodifiableSet()).stream()
                .collect(toUnmodifiableList());
    }

    private Set<String> appSecrets(Application application) {
        LocalResourceMetadata mainResource = application.files().metadata().getMainResource();
        if (mainResource == null) return emptySet();

        YamlObject yaml = yaml(of(mainResource.getPath()));
        List<Object> volumes = yaml.get("spec.template.spec.volumes");
        return volumes.stream()
                .map(YamlObject::yaml)
                .map(volume -> volume.<String>get("secret.secretName"))
                .filter(Objects::nonNull)
                .collect(toUnmodifiableSet());
    }
}
