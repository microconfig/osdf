package io.osdf.api.lib.annotations;

import io.osdf.api.lib.parameter.CommandLineParameter;
import io.osdf.api.lib.parameter.ParamType;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target(PARAMETER)
@Retention(RUNTIME)
public @interface ConsoleParam {
    Class<? extends CommandLineParameter<?>> value();
    ParamType type() default ParamType.OPTIONAL;
}
