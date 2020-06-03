package io.microconfig.osdf.chaos.validators;

import io.microconfig.osdf.chaos.types.Chaos;
import io.microconfig.osdf.exceptions.OSDFException;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

public class PodAndIOChaosIntersectionValidator {
    public static void podAndIOChaosIntersectionCheck(Set<Chaos> chaosSet) {
        Set<String> ioComponents = chaosSet.stream().
                filter(chaos -> chaos.type().equals("io")).
                map(Chaos::getComponents).
                flatMap(Collection::stream).
                collect(Collectors.toUnmodifiableSet());

        Set<String> podComponents = chaosSet.stream().
                filter(chaos -> chaos.type().equals("pod")).
                map(Chaos::getComponents).
                flatMap(Collection::stream).
                collect(Collectors.toUnmodifiableSet());

        if (!Collections.disjoint(ioComponents, podComponents)) {
            throw new OSDFException("Components in io and pod chaos should not intersect");
        }
    }
}
