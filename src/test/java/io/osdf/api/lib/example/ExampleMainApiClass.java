package io.osdf.api.lib.example;

import io.osdf.api.lib.annotations.ApiGroup;
import io.osdf.api.lib.annotations.Named;
import io.osdf.api.lib.annotations.Public;

@Public({"example", "longName"})
public interface ExampleMainApiClass {
    @ApiGroup(ExampleApiClass.class)
    void example();

    @Named(as = "multi word")
    @ApiGroup(ExampleApiClass.class)
    void longName();
}
