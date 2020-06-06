package io.microconfig.osdf.chaos.validators;

import io.microconfig.osdf.chaos.types.Chaos;
import io.microconfig.osdf.chaos.types.ChaosType;
import io.microconfig.osdf.exceptions.OSDFException;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static io.microconfig.osdf.chaos.types.ChaosType.NETWORK;
import static io.microconfig.osdf.chaos.types.ChaosType.POD;
import static java.util.stream.Collectors.toUnmodifiableSet;

public class BasicValidator {
    private BasicValidator() {
        throw new IllegalStateException("Utility class");
    }

    public static void basicCheck(Set<List<Chaos>> chaosSet) {
        chaosSet.parallelStream().forEach(list -> list.forEach(Chaos::check));
    }

    public static void checkNetworkChaosIntersections(Set<List<Chaos>> chaosSet) {
        checkIntersection(chaosSet, NETWORK);
    }

    public static void checkPodChaosIntersections(Set<List<Chaos>> chaosSet) {
        checkIntersection(chaosSet, POD);
    }

    //just check the first list items, because the rest have the same component lists
    private static void checkIntersection(Set<List<Chaos>> chaosSet, ChaosType type) {
        Set<Chaos> filteredSet = chaosSet.stream()
                .map(chaosList -> chaosList.get(0))
                .filter(chaos -> chaos.type().equals(type))
                .collect(toUnmodifiableSet());

        Set<String> components = new HashSet<>();
        filteredSet.forEach(chaos -> {
            if (!Collections.disjoint(components, chaos.getComponents())) {
                throw new OSDFException("Components in " + type + " chaos should not intersect");
            }
            components.addAll(chaos.getComponents());
        });
    }
}
