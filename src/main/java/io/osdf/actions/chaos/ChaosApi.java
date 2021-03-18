package io.osdf.actions.chaos;

import io.osdf.api.lib.annotations.Arg;
import io.osdf.api.lib.annotations.Description;
import io.osdf.api.lib.annotations.Public;

@Public({"run"})
public interface ChaosApi {
    @Description("Run chaos test")
    @Arg(required = "component", d = "Component with chaos description")
    void run(String component);
}
