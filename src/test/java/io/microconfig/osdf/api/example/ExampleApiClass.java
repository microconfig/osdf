package io.microconfig.osdf.api.example;

import io.microconfig.osdf.api.annotation.ApiCommand;
import io.microconfig.osdf.api.annotation.ConsoleParam;
import io.microconfig.osdf.parameters.ArgParameter;

public interface ExampleApiClass {
    class StringArg extends ArgParameter<String> {
        public StringArg() {
            super("first", "f", "description");
        }
    }

    @ApiCommand(description = "Example api method", order = 1)
    void example(@ConsoleParam(StringArg.class) String arg);

    @ApiCommand(description = "Example api method with two words name", order = 2)
    void camelCase(@ConsoleParam(StringArg.class) String arg);
}
