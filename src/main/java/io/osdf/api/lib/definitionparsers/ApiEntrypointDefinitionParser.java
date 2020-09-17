package io.osdf.api.lib.definitionparsers;

import io.osdf.api.lib.definitions.ApiEntrypointDefinition;

public interface ApiEntrypointDefinitionParser {
    ApiEntrypointDefinition parse(Class<?> apiEntrypointClass);
}
