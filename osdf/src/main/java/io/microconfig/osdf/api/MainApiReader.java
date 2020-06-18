package io.microconfig.osdf.api;

import io.microconfig.osdf.api.annotation.ApiCommand;
import io.microconfig.osdf.api.annotation.Hidden;
import io.microconfig.osdf.api.annotation.Group;
import lombok.RequiredArgsConstructor;

import java.lang.reflect.Method;

import static io.microconfig.osdf.api.ImportPrefix.importPrefix;
import static io.microconfig.osdf.utils.StringUtils.pad;
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

        Group anGroup = importMethod.getAnnotation(Group.class);
        String prefix = importPrefix(importMethod).toString();
        ApiReader.reader(anGroup.api())
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
        Group anGroup = method.getAnnotation(Group.class);
        return anGroup.order();
    }
}
