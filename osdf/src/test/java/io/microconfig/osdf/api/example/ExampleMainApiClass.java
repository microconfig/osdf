package io.microconfig.osdf.api.example;

import io.microconfig.osdf.api.annotation.Group;
import io.microconfig.osdf.api.annotation.Named;

public interface ExampleMainApiClass {
    @Group(api = ExampleApiClass.class, order = 1)
    void example();

    @Named(as = "multi word")
    @Group(api = ExampleApiClass.class, order = 2)
    void longName();
}
