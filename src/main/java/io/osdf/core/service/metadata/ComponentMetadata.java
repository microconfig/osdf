package io.osdf.core.service.metadata;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ComponentMetadata {
    private String type;
    private List<LocalResourceMetadata> resourcesMetadata;

    public static ComponentMetadata componentMetadata(String type, List<LocalResourceMetadata> resourcesMetadata) {
        return new ComponentMetadata(type, resourcesMetadata);
    }
}
