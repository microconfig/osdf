package io.osdf.common.exceptions;

public class MicroConfigException extends OSDFException {
    public MicroConfigException(Exception e) {
        super("MicroConfigs are incorrect or don't exist\n" + e.getMessage());
    }
}
