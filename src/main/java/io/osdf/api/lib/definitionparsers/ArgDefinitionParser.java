package io.osdf.api.lib.definitionparsers;

import io.osdf.api.lib.definitions.ArgDefinition;

import java.lang.annotation.Annotation;

public interface ArgDefinitionParser {
    ArgDefinition parse(Annotation arg, Class<?> type);
}
