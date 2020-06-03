package io.microconfig.osdf.chaos.validators;

import io.microconfig.osdf.chaos.types.Chaos;
import io.microconfig.osdf.exceptions.OSDFException;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class BasicValidator {
    public static void basicCheck(Set<Chaos> chaosSet) {
        chaosSet.parallelStream().forEach(Chaos::check);
    }

    public static void checkNetworkChaosIntersections(Set<Chaos> chaosSet) {
        checkIntersection(chaosSet, "network");
    }

    public static void checkPodChaosIntersections(Set<Chaos> chaosSet) {
        checkIntersection(chaosSet, "pod");
    }

    private static void checkIntersection(Set<Chaos> chaosSet, String type) {
        Set<Chaos> filteredSet = chaosSet.stream()
                .filter(chaos -> chaos.type().equals(type))
                .collect(Collectors.toUnmodifiableSet());

        Set<String> components = new HashSet<>();
        filteredSet.forEach(chaos -> {
            if (!Collections.disjoint(components, chaos.getComponents())) {
                throw new OSDFException("Components in " + type + " chaos should not intersect");
            }
            components.addAll(chaos.getComponents());
        });
    }
}
