package io.osdf.api.lib.annotations.parameters;

import io.osdf.api.lib.parser.ArgParser;

import java.lang.annotation.Repeatable;

@Repeatable(RequiredContainer.class)
public @interface Required {
    Class<?> use() default Object.class;
    String n() default "";
    String d() default "";
    Class<? extends ArgParser> p() default ArgParser.class;
}
