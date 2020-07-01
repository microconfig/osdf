package io.osdf.common.utils.mock;

import io.osdf.core.connection.cli.CliOutput;

import static java.util.regex.Pattern.compile;

public interface OCMock {
    CliOutput execute(String command);

    default boolean testCommand(String command) {
        return compile(pattern())
                .matcher(command)
                .matches();
    }

    String pattern();
}
