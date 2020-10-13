package io.osdf.api.parsers;

import io.osdf.api.lib.argparsers.ArgParser;
import io.osdf.common.Credentials;
import io.osdf.common.nexus.NexusArtifact;
import io.osdf.settings.version.OsdfVersion;
import org.junit.jupiter.api.Test;

import java.util.List;

import static java.util.List.of;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class CustomParsersTest {
    @Test
    void returnNull_ifArgIsNull() {
        List<ArgParser<?>> parsers = of(new CredentialsParser(), new NexusArtifactParser(), new OsdfVersionParser());
        parsers.forEach(parser -> assertNull(parser.parse(null)));
    }

    @Test
    void testCredentialParser() {
        assertEquals(new Credentials("user", "pass", "user:pass"),
                new CredentialsParser().parse("user:pass"));
    }

    @Test
    void testNexusArtifactParser() {
        assertEquals(new NexusArtifact("g", "a", "v", null, "zip").getGroup(),
                new NexusArtifactParser().parse("g:a:v").getGroup());
    }

    @Test
    void testOsdfVersionParser() {
        assertEquals(new OsdfVersion(1, 2, 3, null),
                new OsdfVersionParser().parse("1.2.3"));
    }
}