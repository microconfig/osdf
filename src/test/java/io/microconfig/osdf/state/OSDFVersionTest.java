package io.microconfig.osdf.state;

import org.junit.jupiter.api.Test;

import static io.microconfig.osdf.state.OSDFVersion.*;
import static org.junit.jupiter.api.Assertions.*;

class OSDFVersionTest {
    @Test
    void checkFormat() {
        OSDFVersion.fromString("1.2.3");
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
        OSDFVersion version = fromString("2.2.2");

        OSDFVersion newVersion1 = fromString("2.2.3");
        OSDFVersion newVersion2 = fromString("2.3.1");
        OSDFVersion newVersion3 = fromString("3.1.1");
        assertTrue(version.olderThan(newVersion1));
        assertTrue(version.olderThan(newVersion2));
        assertTrue(version.olderThan(newVersion3));

        OSDFVersion olderVersion1 = fromString("2.2.1");
        OSDFVersion olderVersion2 = fromString("2.1.3");
        OSDFVersion olderVersion3 = fromString("1.3.3");
        assertFalse(version.olderThan(olderVersion1));
        assertFalse(version.olderThan(olderVersion2));
        assertFalse(version.olderThan(olderVersion3));

        assertFalse(version.olderThan(version));
    }

    @Test
    void checkMinorThan() {
        OSDFVersion version = fromString("2.2.2");

        OSDFVersion newVersion1 = fromString("2.3.1");
        OSDFVersion newVersion2 = fromString("3.0.0");
        assertTrue(version.hasOlderMinorThan(newVersion1));
        assertTrue(version.hasOlderMinorThan(newVersion2));

        OSDFVersion olderVersion1 = fromString("2.2.5");
        OSDFVersion olderVersion2 = fromString("2.1.5");
        assertFalse(version.hasOlderMinorThan(olderVersion1));
        assertFalse(version.hasOlderMinorThan(olderVersion2));

        assertFalse(version.hasOlderMinorThan(version));
    }
}