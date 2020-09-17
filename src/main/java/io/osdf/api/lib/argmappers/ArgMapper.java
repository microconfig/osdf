package io.osdf.api.lib.argmappers;

import io.osdf.api.lib.definitions.ArgDefinition;
import io.osdf.api.lib.definitions.MethodDefinition;

import java.util.Collection;
import java.util.List;

import static io.osdf.api.lib.argmappers.ApacheFlagArgsMapper.flagArgMapper;
import static io.osdf.api.lib.argmappers.PlainArgsMapper.plainArgsMapper;
import static java.util.stream.Collectors.toList;
import static java.util.stream.IntStream.range;
import static java.util.stream.Stream.of;

public class ArgMapper {
    public static ArgMapper argMapper() {
        return new ArgMapper();
    }

    public List<Object> map(List<String> args, MethodDefinition methodDefinition) {
        int splitIndex = plainCallSplitIndex(args);
        List<String> rawPlainArgs = args.subList(0, splitIndex);
        List<String> rawFlagArgs = args.subList(splitIndex, args.size());

        List<Object> plainArgs = plainArgsMapper().map(rawPlainArgs, methodDefinition);

        List<ArgDefinition> argDefinitions = methodDefinition.getArgs();
        List<ArgDefinition> flagArgDefinitions = argDefinitions.subList(plainArgs.size(), argDefinitions.size());
        List<Object> flagArgs = flagArgMapper(methodDefinition.getMethod().getName(), flagArgDefinitions)
                .parseArgs(rawFlagArgs);

        return of(plainArgs, flagArgs)
                .flatMap(Collection::stream)
                .collect(toList());
    }

    private int plainCallSplitIndex(List<String> args) {
        return range(0, args.size())
                .filter(i -> args.get(i).startsWith("-"))
                .findFirst()
                .orElse(args.size());
    }
}
