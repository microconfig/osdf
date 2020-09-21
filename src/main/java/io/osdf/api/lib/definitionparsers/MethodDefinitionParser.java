package io.osdf.api.lib.definitionparsers;

import io.osdf.api.lib.definitions.MethodDefinition;

import java.lang.reflect.Method;

public interface MethodDefinitionParser {
    MethodDefinition parse(Method method);
}
