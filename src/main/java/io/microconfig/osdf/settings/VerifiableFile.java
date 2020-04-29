package io.microconfig.osdf.settings;

public interface VerifiableFile {
    default boolean verify() {
        return true;
    }
}
