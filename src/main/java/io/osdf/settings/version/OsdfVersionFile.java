package io.osdf.settings.version;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OsdfVersionFile {
    private String version;

    public static OsdfVersionFile osdfVersionFile(OsdfVersion version) {
        return new OsdfVersionFile(version.toString());
    }

    @Override
    public String toString() {
        return "Version: " + version;
    }
}
