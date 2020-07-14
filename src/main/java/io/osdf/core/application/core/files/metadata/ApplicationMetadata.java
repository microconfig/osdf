package io.osdf.core.application.core.files.metadata;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationMetadata {
    private String type;
    private List<LocalResourceMetadata> resourcesMetadata;
    private LocalResourceMetadata mainResource;

    public static ApplicationMetadata serviceMetadata(String type, List<LocalResourceMetadata> resourcesMetadata,
                                                      LocalResourceMetadata mainResource) {
        return new ApplicationMetadata(type, resourcesMetadata, mainResource);
    }
}
