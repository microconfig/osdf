package io.osdf.api.lib.example;

import io.osdf.api.lib.annotations.ApiGroup;
import io.osdf.api.lib.annotations.Named;

public interface ExampleMainApiClass {
    @ApiGroup(api = ExampleApiClass.class, order = 1)
    void example();

    @Named(as = "multi word")
    @ApiGroup(api = ExampleApiClass.class, order = 2)
    void longName();
}
