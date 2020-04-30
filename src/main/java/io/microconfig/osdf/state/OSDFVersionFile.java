package io.microconfig.osdf.state;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OSDFVersionFile {
    private String version;

    public static OSDFVersionFile osdfVersionFile(OSDFVersion version) {
        return new OSDFVersionFile(version.toString());
    }
}
