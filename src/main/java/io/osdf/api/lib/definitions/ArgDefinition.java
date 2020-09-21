package io.osdf.api.lib.definitions;

import io.osdf.api.lib.argparsers.ArgParser;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static io.osdf.api.lib.definitions.ArgType.FLAG;
import static io.osdf.api.lib.definitions.ArgType.OPTIONAL;

@Getter
@RequiredArgsConstructor
public class ArgDefinition {
    private final String name;
    private final String shortName;
    private final String description;
    private final ArgType argType;
    private final Class<?> type;
    private final ArgParser<?> parser;

    public String usage() {
        if (argType == FLAG) return flagUsage();
        String argUsage = argUsage();
        return argType == OPTIONAL ? "[" + argUsage + "]" : argUsage;
    }

    private String argUsage() {
        String argUsage = "-" + shortName + " " + name;
        return type.isAssignableFrom(List.class) ? argUsage + "..." : argUsage;
    }

    private String flagUsage() {
        return "[--" + name + "/-" + shortName + "]";
    }
}
