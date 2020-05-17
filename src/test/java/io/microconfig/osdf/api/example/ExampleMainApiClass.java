package io.microconfig.osdf.api.example;

import io.microconfig.osdf.api.annotation.Import;
import io.microconfig.osdf.api.annotation.Named;

public interface ExampleMainApiClass {
    @Import(api = ExampleApiClass.class, order = 1)
    void example();

    @Named(as = "multi word")
    @Import(api = ExampleApiClass.class, order = 2)
    void longName();
}
