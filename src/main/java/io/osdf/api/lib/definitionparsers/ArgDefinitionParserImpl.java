package io.osdf.api.lib.definitionparsers;

import io.osdf.api.lib.ApiException;
import io.osdf.api.lib.definitions.ArgType;
import io.osdf.api.lib.annotations.Arg;
import io.osdf.api.lib.argparsers.ArgParser;
import io.osdf.api.lib.argparsers.DefaultParser;
import io.osdf.api.lib.definitions.ArgDefinition;
import lombok.Getter;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;

import static io.osdf.api.lib.definitions.ArgType.*;

public class ArgDefinitionParserImpl implements ArgDefinitionParser {
    @Override
    public ArgDefinition parse(Annotation annotation, Class<?> type) {
        Arg arg = (Arg) annotation;
        ArgType argType = typeOf(arg);
        Names names = new Names(arg, argType);
        return new ArgDefinition(names.getName(), names.getShortName(), arg.d(), argType, type, argParser(type, arg.p()));
    }

    private ArgType typeOf(Arg arg) {
        if (!arg.required().isEmpty()) return REQUIRED;
        if (!arg.optional().isEmpty()) return OPTIONAL;
        if (!arg.flag().isEmpty()) return FLAG;
        throw new ApiException("Set arg name using required, optional or flag in Arg annotation");
    }

    private ArgParser<?> argParser(Class<?> type, Class<? extends ArgParser<?>> parserClass) {
        if (parserClass == DefaultParser.class) return new DefaultParser(type);
        try {
            return parserClass.getConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new ApiException("Define empty constructor for parser " + parserClass.getSimpleName());
        }
    }

    @Getter
    private static class Names {
        private String name;
        private String shortName;

        public Names(Arg arg, ArgType argType) {
            String fullName = fullName(arg, argType);
            setNameAndShortName(fullName);
        }

        private void setNameAndShortName(String fullName) {
            String[] split = fullName.split("/");
            if (split.length == 1) {
                name = split[0];
                shortName = name.substring(0, 1);
            } else {
                name = split[1];
                shortName = split[0];
            }
        }

        private String fullName(Arg arg, ArgType type) {
            switch (type) {
                case REQUIRED: return arg.required();
                case OPTIONAL: return arg.optional();
                case FLAG: return arg.flag();
                default: throw new ApiException("Unknown arg type " + type);
            }
        }
    }
}
