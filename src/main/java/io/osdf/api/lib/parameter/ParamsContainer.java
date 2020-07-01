package io.osdf.api.lib.parameter;

import lombok.RequiredArgsConstructor;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;

import java.util.List;

@RequiredArgsConstructor
public class ParamsContainer {
    private final String methodName;
    private final List<CommandLineParameter<?>> parameters;

    public Object get(Class<? extends CommandLineParameter<?>> clazz) {
        for (CommandLineParameter<?> parameter : parameters) {
            if (clazz.isInstance(parameter)) {
                return parameter.get();
            }
        }
        return null;
    }

    public void printHelp() {
        Options options = new Options();
        parameters
                .stream()
                .map(CommandLineParameter::toOption)
                .forEach(options::addOption);
        new HelpFormatter().printHelp("osdf " + methodName, options, true);
    }
}
