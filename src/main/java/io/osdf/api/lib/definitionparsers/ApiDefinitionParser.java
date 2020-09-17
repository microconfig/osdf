package io.osdf.api.lib.definitionparsers;

import io.osdf.api.lib.definitions.ApiDefinition;

public interface ApiDefinitionParser {
    ApiDefinition parse(Class<?> apiClass);
}
