package io.osdf.api.lib.definitions;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static java.util.Collections.emptyList;
import static java.util.List.of;

@Getter
@RequiredArgsConstructor
public class ApiGroupDefinition {
    private final String name;
    private final String prefix;
    private final ApiDefinition apiDefinition;

    public List<String> removePrefix(List<String> args) {
        if (prefix.isEmpty()) return args;
        List<String> prefixes = of(prefix.split(" "));
        if (args.subList(0, prefixes.size()).equals(prefixes)) return args.subList(prefixes.size(), args.size());
        return emptyList();
    }
}
