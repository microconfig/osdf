package io.osdf.api.lib.definitionparsers;

import io.osdf.api.lib.definitions.ApiGroupDefinition;

import java.lang.reflect.Method;

public interface ApiGroupDefinitionParser {
    ApiGroupDefinition parse(Method method);
}
