package io.osdf.settings.version;

import io.osdf.settings.version.OsdfVersion;
import org.junit.jupiter.api.Test;

import static io.osdf.settings.version.OsdfVersion.*;
import static org.junit.jupiter.api.Assertions.*;

class OSDFVersionTest {
    @Test
    void checkFormat() {
        OsdfVersion.fromString("1.2.3");
        assertBadFormat("1.2.b");
        assertBadFormat("badFormat");
        assertBadFormat("1.2");
        assertBadFormat("1");
    }

    private void assertBadFormat(String format) {
        assertThrows(RuntimeException.class, () -> fromString(format));
    }

    @Test
    void checkOlderThan() {
        OsdfVersion version = fromString("2.2.2");

        OsdfVersion newVersion1 = fromString("2.2.3");
        OsdfVersion newVersion2 = fromString("2.3.1");
        OsdfVersion newVersion3 = fromString("3.1.1");
        assertTrue(version.olderThan(newVersion1));
        assertTrue(version.olderThan(newVersion2));
        assertTrue(version.olderThan(newVersion3));

        OsdfVersion olderVersion1 = fromString("2.2.1");
        OsdfVersion olderVersion2 = fromString("2.1.3");
        OsdfVersion olderVersion3 = fromString("1.3.3");
        assertFalse(version.olderThan(olderVersion1));
        assertFalse(version.olderThan(olderVersion2));
        assertFalse(version.olderThan(olderVersion3));

        assertFalse(version.olderThan(version));
    }

    @Test
    void checkMinorThan() {
        OsdfVersion version = fromString("2.2.2");

        OsdfVersion newVersion1 = fromString("2.3.1");
        OsdfVersion newVersion2 = fromString("3.0.0");
        assertTrue(version.hasOlderMinorThan(newVersion1));
        assertTrue(version.hasOlderMinorThan(newVersion2));

        OsdfVersion olderVersion1 = fromString("2.2.5");
        OsdfVersion olderVersion2 = fromString("2.1.5");
        assertFalse(version.hasOlderMinorThan(olderVersion1));
        assertFalse(version.hasOlderMinorThan(olderVersion2));

        assertFalse(version.hasOlderMinorThan(version));
    }
}