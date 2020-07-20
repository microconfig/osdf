package io.osdf.api.lib.example;

import io.osdf.api.lib.annotations.ApiCommand;
import io.osdf.api.lib.annotations.ConsoleParam;
import io.osdf.api.lib.parameter.ArgParameter;

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
