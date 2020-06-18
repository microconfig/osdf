package io.microconfig.osdf.api.annotation;

import io.microconfig.osdf.parameters.CommandLineParameter;
import io.microconfig.osdf.parameters.ParamType;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static io.microconfig.osdf.parameters.ParamType.OPTIONAL;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target(PARAMETER)
@Retention(RUNTIME)
public @interface ConsoleParam {
    Class<? extends CommandLineParameter<?>> value();
    ParamType type() default OPTIONAL;
}
