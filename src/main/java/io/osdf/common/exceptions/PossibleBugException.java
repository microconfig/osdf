package io.osdf.common.exceptions;

public class PossibleBugException extends OSDFException {
    public PossibleBugException(String message, Throwable t) {
        super(message + "\nCause: " + t.getClass().getSimpleName() + " " + t.getMessage(), t);
    }
}
