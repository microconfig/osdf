package io.osdf.common.utils;

import io.osdf.common.exceptions.PossibleBugException;
import io.osdf.actions.system.install.OSDFInstaller;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;

import static java.lang.System.getProperty;
import static java.nio.file.Path.of;
import static java.nio.file.Paths.get;

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

    public static String pathToJava() {
        return get(getProperty("java.home").replace(" ", "\\ "), "bin", "java").toString();
    }
}
