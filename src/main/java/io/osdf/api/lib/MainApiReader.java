package io.osdf.api.lib;

import io.osdf.api.lib.annotations.ApiCommand;
import io.osdf.api.lib.annotations.Hidden;
import io.osdf.api.lib.annotations.Import;
import lombok.RequiredArgsConstructor;

import java.lang.reflect.Method;

import static io.osdf.api.lib.ImportPrefix.importPrefix;
import static io.osdf.common.utils.StringUtils.pad;
import static io.microconfig.utils.Logger.announce;
import static io.microconfig.utils.Logger.info;
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
    }

    private String capitalize(String name) {
        return name.substring(0, 1).toUpperCase() + name.substring(1);
    }

    private void printMethodDescription(String prefix, Method method) {
        String description = method.getAnnotation(ApiCommand.class).description();
        info(pad(" " + prefixedName(prefix, method), 50) + description);
    }

    private String prefixedName(String prefix, Method method) {
        return prefix.isEmpty() ? method.getName() : prefix + " " + method.getName();
    }

    private static int importOrder(Method method) {
        Import anImport = method.getAnnotation(Import.class);
        return anImport.order();
    }
}
