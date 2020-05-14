package io.microconfig.osdf.istio.faults;

import lombok.RequiredArgsConstructor;

import static java.util.Map.of;

@RequiredArgsConstructor
public class DelayFault implements Fault {
    private final String delay;
    private final Integer percentageValue;

    public static Fault delayFault(String delay, Integer percentageValue) {
        return new DelayFault(delay, percentageValue);
    }

    @Override
    public Object toYaml() {
        return of(
                "delay", of(
                        "fixedDelay", delay,
                        "percentage", of(
                                "value", percentageValue
                        )
                )
        );
    }
}
