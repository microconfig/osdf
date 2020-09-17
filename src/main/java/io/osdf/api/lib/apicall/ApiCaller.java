package io.osdf.api.lib.apicall;

import java.util.List;

public interface ApiCaller {
    void call(Class<?> apiEntrypointClass, List<String> args);
}
