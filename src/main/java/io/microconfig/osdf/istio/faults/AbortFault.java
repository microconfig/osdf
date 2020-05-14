package io.microconfig.osdf.istio.faults;

import lombok.RequiredArgsConstructor;

import static java.util.Map.of;

@RequiredArgsConstructor
public class AbortFault implements Fault {
    private final String httpStatus;
    private final Integer percentageValue;

    public static Fault abortFault(String httpStatus, Integer percentageValue) {
        return new AbortFault(httpStatus, percentageValue);
    }

    @Override
    public Object toYaml() {
        return of(
                "abort", of(
                        "httpStatus", httpStatus,
                        "percentage", of(
                                "value", percentageValue
                        )
                )
        );

    }
}
