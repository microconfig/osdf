package io.osdf.api.lib;

import io.osdf.api.lib.annotations.ConsoleParam;
import io.osdf.api.lib.annotations.Hidden;
import io.osdf.api.lib.annotations.Import;
import io.osdf.api.lib.parameter.CommandLineParameter;
import io.osdf.api.lib.parameter.FlagParameter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.cli.Option;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static io.microconfig.utils.Logger.announce;
import static io.microconfig.utils.Logger.info;
import static io.osdf.api.lib.ImportPrefix.importPrefix;
import static io.osdf.api.lib.parameter.CommandLineParameter.createFrom;
import static io.osdf.api.lib.parameter.ParamType.OPTIONAL;
import static io.osdf.common.utils.ReflectionUtils.processAnnotation;
import static io.osdf.common.utils.StringUtils.pad;
import static java.lang.String.join;
import static java.util.Arrays.stream;
import static java.util.Comparator.comparingInt;

@RequiredArgsConstructor
public class MainApiReader {
    private final Class<?> apiClass;

    public static MainApiReader apiInfo(Class<?> apiClass) {
        return new MainApiReader(apiClass);
    }

    public void printHelp() {
        stream(apiClass.getMethods())
                .filter(m -> m.getAnnotation(Hidden.class) == null)
                .sorted(comparingInt(MainApiReader::importOrder))
                .forEach(this::printHelpForImport);
    }

    private void printHelpForImport(Method importMethod) {
        announce(capitalize(importMethod.getName()));

        Import anImport = importMethod.getAnnotation(Import.class);
        String prefix = importPrefix(importMethod).toString();
        ApiReader.reader(anImport.api())
                .methods()
                .forEach(method -> printMethodDescription(prefix, method));
        info("");
    }

    private String capitalize(String name) {
        return name.substring(0, 1).toUpperCase() + name.substring(1);
    }

    private void printMethodDescription(String prefix, Method method) {
        List<String> descriptions = new ArrayList<>();

        processAnnotation(method, ConsoleParam.class, consoleParam -> {
            CommandLineParameter<?> param = createFrom(consoleParam);
            Option option = param.toOption();
            if (param instanceof FlagParameter) {
                descriptions.add(flagParamDescription(option));
            } else {
                descriptions.add(argParamDescription(consoleParam, option));
            }
        });

        info(pad(" " + prefixedName(prefix, method), 30) + join(" ", descriptions));
    }

    private String flagParamDescription(Option option) {
        return "[--" + option.getLongOpt() + "/-" + option.getOpt() + "]";
    }

    private String argParamDescription(ConsoleParam consoleParam, Option option) {
        String argDescription = "-" + option.getOpt() + " " + option.getLongOpt();
        return consoleParam.type() == OPTIONAL ? "[" + argDescription + "]" : argDescription;
    }

    private String prefixedName(String prefix, Method method) {
        return prefix.isEmpty() ? method.getName() : prefix + " " + method.getName();
    }

    private static int importOrder(Method method) {
        Import anImport = method.getAnnotation(Import.class);
        return anImport.order();
    }
}
