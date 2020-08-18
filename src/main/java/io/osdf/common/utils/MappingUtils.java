package io.osdf.common.utils;

import io.osdf.common.exceptions.OSDFException;

import java.util.Map;
import java.util.function.Supplier;

public class MappingUtils {
    public static <F, T> T fromMapping(F obj, Map<Class<? extends F>, Supplier<? extends T>> mapping) {
        return mapping.entrySet().stream()
                .filter(entry -> entry.getKey().isInstance(obj))
                .findFirst().orElseThrow(() -> new OSDFException("Unknown type: " + obj.getClass().getSimpleName()))
                .getValue().get();
    }
}
