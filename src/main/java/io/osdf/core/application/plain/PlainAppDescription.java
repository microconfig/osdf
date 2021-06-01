package io.osdf.core.application.plain;

import io.osdf.core.application.core.files.ApplicationFiles;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static io.osdf.actions.management.deploy.smart.hash.ResourcesHashComputer.resourcesHashComputer;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PlainAppDescription {
    private String configHash;

    public static PlainAppDescription from(ApplicationFiles files) {
        String hash = resourcesHashComputer().currentHash(files);
        return new PlainAppDescription(hash);
    }
}
