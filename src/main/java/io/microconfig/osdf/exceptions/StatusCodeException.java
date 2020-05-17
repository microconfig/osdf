package io.microconfig.osdf.exceptions;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class StatusCodeException extends OSDFException {
    @Getter
    private final int statusCode;
}
