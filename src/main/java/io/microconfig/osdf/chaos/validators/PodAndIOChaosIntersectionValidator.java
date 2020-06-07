package io.microconfig.osdf.chaos.validators;

import io.microconfig.osdf.chaos.types.Chaos;
import io.microconfig.osdf.exceptions.OSDFException;
import lombok.RequiredArgsConstructor;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import static io.microconfig.osdf.chaos.types.ChaosType.IO;
import static io.microconfig.osdf.chaos.types.ChaosType.POD;
import static java.util.Collections.disjoint;
import static java.util.stream.Collectors.toUnmodifiableSet;

@RequiredArgsConstructor
public class PodAndIOChaosIntersectionValidator {
    private final Set<List<Chaos>> chaosSet;

    public static PodAndIOChaosIntersectionValidator podAndIOChaosIntersectionValidator(Set<List<Chaos>> chaosSet) {
        return new PodAndIOChaosIntersectionValidator(chaosSet);
    }

    //just check the first list items, because the rest have the same component lists
    public void podAndIOChaosIntersectionCheck() {
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
