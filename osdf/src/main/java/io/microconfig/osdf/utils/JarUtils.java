package io.microconfig.osdf.utils;

import io.microconfig.osdf.exceptions.PossibleBugException;
import io.microconfig.osdf.install.OSDFInstaller;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;

import static java.nio.file.Path.of;

public class JarUtils {
    public static Path jarPath() {
        try {
            URI uri = OSDFInstaller.class.getProtectionDomain().getCodeSource().getLocation().toURI();
            return of(uri).toAbsolutePath();
        } catch (URISyntaxException e) {
            throw new PossibleBugException("Couldn't get jar path", e);
        }
    }

    public static boolean isJar() {
        try {
            URI uri = OSDFInstaller.class.getProtectionDomain().getCodeSource().getLocation().toURI();
            return of(uri).getFileName().toString().endsWith(".jar");
        } catch (URISyntaxException e) {
            throw new PossibleBugException("Couldn't get jar path", e);
        }
    }
}
