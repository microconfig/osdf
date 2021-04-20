package io.osdf.actions.chaos.assaults;

public interface Assault {
    void start();

    void stop();

    default void clear() {
        //no clearing needed
    }
}
