package io.osdf.core.service.metadata;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LocalResourceMetadata {
    private String kind;
    private String name;
    private String path;

    public static LocalResourceMetadata create(String kind, String name, String path) {
        return new LocalResourceMetadata(kind.toLowerCase(), name, path);
    }
}
