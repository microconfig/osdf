package io.osdf.api.lib;

public class ApiException extends RuntimeException {
    public ApiException(String message) {
        super(message);
    }
}
