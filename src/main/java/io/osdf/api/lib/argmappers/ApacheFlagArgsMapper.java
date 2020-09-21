package io.osdf.api.lib.argmappers;

import io.osdf.api.lib.ApiException;
import io.osdf.api.lib.argparsers.ArgParser;
import io.osdf.api.lib.definitions.ArgDefinition;
import lombok.RequiredArgsConstructor;
import org.apache.commons.cli.*;

import java.util.List;

import static io.microconfig.utils.Logger.info;
import static io.osdf.api.lib.definitions.ArgType.FLAG;
import static io.osdf.api.lib.definitions.ArgType.REQUIRED;
import static java.util.stream.Collectors.toList;

@RequiredArgsConstructor
public class ApacheFlagArgsMapper {
    private final Options options;
    private final List<ArgDefinition> argDefinitions;
    private final String methodName;

    public static ApacheFlagArgsMapper flagArgMapper(String methodName, List<ArgDefinition> argDefinitions) {
        return new ApacheFlagArgsMapper(options(argDefinitions), argDefinitions, methodName);
    }

    public List<Object> parseArgs(List<String> args) {
        CommandLine commandLine = runParser(args);
        return argDefinitions.stream()
                .map(definition -> parse(definition, commandLine))
                .collect(toList());
    }

    public void printHelp() {
        new HelpFormatter().printHelp("osdf " + methodName, options, true);
    }

    private Object parse(ArgDefinition definition, CommandLine commandLine) {
        if (definition.getArgType() == FLAG) return commandLine.hasOption(definition.getName());
        String optionValue = commandLine.getOptionValue(definition.getName());
        ArgParser<?> parser = definition.getParser();
        return List.class.isAssignableFrom(definition.getType()) ? parser.parseList(optionValue) : parser.parse(optionValue);
    }

    private static Options options(List<ArgDefinition> argDefinitions) {
        Options options = new Options();
        argDefinitions.forEach(arg -> options.addOption(toOption(arg)));
        return options;
    }

    private static Option toOption(ArgDefinition argDefinition) {
        Option option = new Option(argDefinition.getShortName(), argDefinition.getName(), argDefinition.getArgType() != FLAG, argDefinition.getDescription());
        option.setRequired(argDefinition.getArgType() == REQUIRED);
        return option;
    }

    private CommandLine runParser(List<String> args) {
        try {
            return new DefaultParser().parse(options, args.toArray(String[]::new));
        } catch (ParseException e) {
            info(e.getMessage());
            printHelp();
            throw new ApiException(e.getMessage());
        }
    }
}
