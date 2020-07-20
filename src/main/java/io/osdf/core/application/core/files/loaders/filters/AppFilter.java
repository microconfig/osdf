package io.osdf.core.application.core.files.loaders.filters;

import io.osdf.core.local.component.ComponentDir;

import java.util.function.Predicate;

import static java.nio.file.Files.exists;

public class AppFilter implements Predicate<ComponentDir> {
    public static AppFilter isApp() {
        return new AppFilter();
    }

    @Override
    public boolean test(ComponentDir componentDir) {
        return exists(componentDir.getPath("resources")) && exists(componentDir.getPath("deploy.yaml"));
    }
}
