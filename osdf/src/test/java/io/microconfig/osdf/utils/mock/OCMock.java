package io.microconfig.osdf.utils.mock;

import io.cluster.old.cluster.commandline.CommandLineOutput;

import static java.util.regex.Pattern.compile;

public interface OCMock {
    CommandLineOutput execute(String command);

    default boolean testCommand(String command) {
        return compile(pattern())
                .matcher(command)
                .matches();
    }

    String pattern();
}
