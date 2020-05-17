package io.microconfig.osdf.exceptions;

public class MicroConfigException extends OSDFException {
    public MicroConfigException() {
        super("MicroConfigs are incorrect or don't exist");
    }
}
