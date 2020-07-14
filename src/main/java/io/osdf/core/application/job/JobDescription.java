package io.osdf.core.application.job;

import io.osdf.core.application.core.description.ResourceDescription;
import io.osdf.core.application.core.files.ApplicationFiles;
import io.osdf.core.application.core.files.metadata.ApplicationMetadata;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.nio.file.Path;

import static io.osdf.core.application.core.description.ResourceDescription.fromLocalResource;
import static java.nio.file.Path.of;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class JobDescription {
    private ResourceDescription job;

    public static JobDescription from(ApplicationFiles files) {
        ApplicationMetadata metadata = files.metadata();
        Path job = of(metadata.getMainResource().getPath());
        return new JobDescription(fromLocalResource(job));
    }
}
