package io.microconfig.osdf.exceptions;

public class OSDFException extends RuntimeException {
    public OSDFException() {
        super();
    }
    public OSDFException(String message) {
        super(message);
    }
    public OSDFException(String message, Throwable t) {
        super(message, t);
    }
}
