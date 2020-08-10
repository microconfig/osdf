package io.osdf.api.lib.annotations.parameters;

import java.lang.annotation.Repeatable;

@Repeatable(FlagContainer.class)
public @interface Flag {
    Class<?> use() default Object.class;
    String n() default "";
    String d() default "";
}
