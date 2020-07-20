package io.osdf.api.lib.example;

import io.osdf.api.lib.annotations.Import;
import io.osdf.api.lib.annotations.Named;

public interface ExampleMainApiClass {
    @Import(api = ExampleApiClass.class, order = 1)
    void example();

    @Named(as = "multi word")
    @Import(api = ExampleApiClass.class, order = 2)
    void longName();
}
