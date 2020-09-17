package io.osdf.api.lib.example;

import io.osdf.api.lib.annotations.Arg;
import io.osdf.api.lib.annotations.Description;
import io.osdf.api.lib.annotations.Public;

@Public({"example", "camelCase"})
public interface ExampleApiClass {
    @Description("Example api method")
    @Arg(optional = "first", d = "description")
    void example(String arg);

    @Description("Example api method with two words name")
    @Arg(optional = "first", d = "description")
    void camelCase(String arg);
}
