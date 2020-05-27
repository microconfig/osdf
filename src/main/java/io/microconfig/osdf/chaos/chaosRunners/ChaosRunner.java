package io.microconfig.osdf.chaos.chaosRunners;

import io.microconfig.osdf.chaos.ChaosSet;

import java.util.List;

public interface ChaosRunner {

    void run(List<String> components, ChaosSet chaosSet, Integer severity, Integer duration);

    void stop(List<String> components);
}
