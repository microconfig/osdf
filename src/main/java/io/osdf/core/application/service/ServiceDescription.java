package io.osdf.core.application.service;

import io.osdf.core.application.ResourceDescription;
import io.osdf.core.application.local.ApplicationFiles;
import io.osdf.core.application.local.metadata.ApplicationMetadata;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.nio.file.Path;

import static io.osdf.core.application.ResourceDescription.fromLocalResource;
import static java.nio.file.Path.of;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ServiceDescription {
    private ResourceDescription deployment;

    public static ServiceDescription from(ApplicationFiles files) {
        ApplicationMetadata metadata = files.metadata();
        Path deploymentPath = of(metadata.getMainResource().getPath());
        return new ServiceDescription(fromLocalResource(deploymentPath));
    }
}