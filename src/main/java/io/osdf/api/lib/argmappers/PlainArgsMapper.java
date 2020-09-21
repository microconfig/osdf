package io.osdf.api.lib.argmappers;

import io.osdf.api.lib.argparsers.ArgParser;
import io.osdf.api.lib.definitions.ArgDefinition;
import io.osdf.api.lib.definitions.MethodDefinition;

import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toUnmodifiableList;

public class PlainArgsMapper {
    public static PlainArgsMapper plainArgsMapper() {
        return new PlainArgsMapper();
    }

    public List<Object> map(List<String> args, MethodDefinition methodDefinition) {
        List<ArgDefinition> argDefinitions = methodDefinition.getArgs();
        List<ArgParser<?>> parsers = argDefinitions.stream()
                .map(ArgDefinition::getParser)
                .collect(toUnmodifiableList());
        Class<?>[] parameterTypes = methodDefinition.getMethod().getParameterTypes();

        return doMap(args, parsers, parameterTypes);
    }

    private List<Object> doMap(List<String> args, List<ArgParser<?>> parsers, Class<?>[] parameterTypes) {
        int ind = 0;
        List<Object> result = new ArrayList<>();
        for (Class<?> type : parameterTypes) {
            if (ind == args.size()) break;
            ArgParser<?> parser = parsers.get(ind);
            if (List.class.isAssignableFrom(type)) {
                List<?> parsedArgs = parser.parseList(args.subList(ind, args.size()));
                result.add(parsedArgs);
                ind = args.size();
            } else {
                result.add(parser.parse(args.get(ind)));
                ind++;
            }
        }
        return result;
    }
}
