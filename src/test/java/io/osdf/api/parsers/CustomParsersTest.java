package io.osdf.api.parsers;

import io.osdf.api.lib.argparsers.ArgParser;
import org.junit.jupiter.api.Test;

import java.util.List;

import static java.util.List.of;
import static org.junit.jupiter.api.Assertions.assertNull;

class CustomParsersTest {
    @Test
    void returnNull_ifArgIsNull() {
        List<ArgParser<?>> parsers = of(new CredentialsParser(), new NexusArtifactParser(), new OsdfVersionParser());
        parsers.forEach(parser -> assertNull(parser.parse(null)));
    }
}