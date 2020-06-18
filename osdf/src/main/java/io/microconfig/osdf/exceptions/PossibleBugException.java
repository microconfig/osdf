package io.microconfig.osdf.exceptions;

public class PossibleBugException extends OSDFException {
    public PossibleBugException(String message, Throwable t) {
        super(message + "\nCause: " + t.getClass().getSimpleName() + " " + t.getMessage(), t);
    }
    public PossibleBugException(String message) {
        super(message);
    }
}
