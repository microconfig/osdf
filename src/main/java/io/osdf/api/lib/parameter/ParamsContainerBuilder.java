package io.osdf.api.lib.parameter;

import io.osdf.common.exceptions.PossibleBugException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.cli.*;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import static io.microconfig.utils.Logger.info;

@RequiredArgsConstructor
public class ParamsContainerBuilder {
    private final Options options;
    private final List<CommandLineParameter<?>> parameters;
    private final List<ParamType> types;
    private final String methodName;

    public static ParamsContainerBuilder builder(String methodName) {
        return new ParamsContainerBuilder(new Options(), new ArrayList<>(), new ArrayList<>(), methodName);
    }

    public void add(Class<? extends CommandLineParameter<?>> clazz, ParamType type) {
        CommandLineParameter<?> parameter = createParameter(clazz);
        parameter.toOption().setRequired(type == ParamType.REQUIRED);
        options.addOption(parameter.toOption());
        parameters.add(parameter);
        types.add(type);
    }

    public ParamsContainer build(String[] args) throws ParseException {
        CommandLine commandLine = runParser(args);
        parameters.forEach(param -> setParameter(commandLine, param));
        return new ParamsContainer(methodName, parameters);
    }

    private void setParameter(CommandLine commandLine, CommandLineParameter<?> param) {
        if (param instanceof FlagParameter) {
            param.set(commandLine.hasOption(param.name()) ? "true" : "false");
            return;
        }
        param.set(commandLine.getOptionValue(param.name()));
    }

    public ParamsContainer build() {
        return new ParamsContainer(methodName, parameters);
    }

    private CommandLineParameter<?> createParameter(Class<? extends CommandLineParameter<?>> clazz) {
        try {
            return clazz.getConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new PossibleBugException("Couldn't find default constructor for parameter " + clazz.getSimpleName(), e);
        }
    }

    private CommandLine runParser(String[] args) throws ParseException {
        try {
            return new DefaultParser().parse(options, args);
        } catch (ParseException e) {
            info(e.getMessage());
            new HelpFormatter().printHelp("osdf " + methodName, options, true);
            throw new ParseException(e.getMessage());
        }
    }
}
