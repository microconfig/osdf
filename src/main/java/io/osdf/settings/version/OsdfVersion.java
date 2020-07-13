package io.osdf.settings.version;

import io.osdf.common.exceptions.OSDFException;
import io.osdf.core.local.microconfig.property.PropertyGetter;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

import java.nio.file.Path;

import static io.osdf.settings.version.OsdfArtifactFromConfigs.osdfArtifact;
import static io.osdf.common.SettingsFile.settingsFile;
import static io.osdf.common.utils.StringUtils.castToInteger;

@RequiredArgsConstructor
@EqualsAndHashCode
public class OsdfVersion {
    private final int major;
    private final int minor;
    private final int patch;
    private final String suffix;

    public static OsdfVersion fromJarPath(Path path) {
        String filename = path.getFileName().toString();
        filename = filename.substring(0, filename.length() - 4);
        String[] dashSplit = filename.split("-");
        if (dashSplit.length != 2) throw new OSDFException("Bad jar file name. Should be <name>-<version>.jar");
        return fromString(dashSplit[1]);
    }

    public static OsdfVersion fromSettings(Path path) {
        String version = settingsFile(OsdfVersionFile.class, path).getSettings().getVersion();
        return fromString(version);
    }

    public static OsdfVersion fromConfigs(PropertyGetter propertyGetter) {
        return fromString(osdfArtifact(propertyGetter).version());
    }

    public static OsdfVersion fromString(String s) {
        if (s == null) throw exception("<null>");

        String[] split = s.split("\\.");
        if (split.length != 3 && split.length != 4) throw exception(s);

        Integer major = castToInteger(split[0]);
        Integer minor = castToInteger(split[1]);
        Integer patch = castToInteger(split[2]);
        if (major == null || minor == null || patch == null) throw exception(s);
        return new OsdfVersion(major, minor, patch, split.length == 4 ? split[3] : null);
    }

    private static OSDFException exception(String s) {
        return new OSDFException("Bad version format " + s);
    }

    public boolean olderThan(OsdfVersion other) {
        if (major != other.major) return major < other.major;
        if (minor != other.minor) return minor < other.minor;
        if (patch != other.patch) return patch < other.patch;
        return false;
    }

    public boolean hasOlderMinorThan(OsdfVersion other) {
        if (major != other.major) return major < other.major;
        if (minor != other.minor) return minor < other.minor;
        return false;
    }

    @Override
    public String toString() {
        return major + "." + minor + "." + patch + (suffix != null ? "." + suffix : "");
    }
}
