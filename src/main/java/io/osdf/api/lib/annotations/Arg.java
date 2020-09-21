package io.osdf.api.lib.annotations;

import io.osdf.api.lib.argparsers.ArgParser;
import io.osdf.api.lib.argparsers.DefaultParser;

import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Repeatable(ArgContainer.class)
@Target(METHOD)
@Retention(RUNTIME)
public @interface Arg {
    String required() default "";
    String optional() default "";
    String flag() default "";
    String d() default "";
    Class<? extends ArgParser<?>> p() default DefaultParser.class;
}


