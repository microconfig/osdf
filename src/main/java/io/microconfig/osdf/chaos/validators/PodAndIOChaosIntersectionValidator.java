package io.microconfig.osdf.chaos.validators;

import io.microconfig.osdf.chaos.types.Chaos;
import io.microconfig.osdf.exceptions.OSDFException;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import static io.microconfig.osdf.chaos.types.ChaosType.IO;
import static io.microconfig.osdf.chaos.types.ChaosType.POD;
import static java.util.Collections.disjoint;
import static java.util.stream.Collectors.toUnmodifiableSet;

public class PodAndIOChaosIntersectionValidator {
    private PodAndIOChaosIntersectionValidator() {
        throw new IllegalStateException("Utility class");
    }

    //just check the first list items, because the rest have the same component lists
    public static void podAndIOChaosIntersectionCheck(Set<List<Chaos>> chaosSet) {
        Set<String> ioComponents = chaosSet.stream()
                .map(chaosList -> chaosList.get(0))
                .filter(chaos -> chaos.type().equals(IO))
                .map(Chaos::getComponents)
                .flatMap(Collection::stream)
                .collect(toUnmodifiableSet());

        Set<String> podComponents = chaosSet.stream()
                .map(chaosList -> chaosList.get(0))
                .filter(chaos -> chaos.type().equals(POD))
                .map(Chaos::getComponents)
                .flatMap(Collection::stream)
                .collect(toUnmodifiableSet());

        if (!disjoint(ioComponents, podComponents)) {
            throw new OSDFException("Components in io and pod chaos should not intersect");
        }
    }
}
