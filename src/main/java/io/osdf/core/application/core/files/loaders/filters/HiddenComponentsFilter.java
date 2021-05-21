package io.osdf.core.application.core.files.loaders.filters;

import io.osdf.core.application.core.files.ApplicationFiles;

import java.util.function.Predicate;

public class HiddenComponentsFilter implements Predicate<ApplicationFiles> {
    public static HiddenComponentsFilter hiddenComponentsFilter() {
        return new HiddenComponentsFilter();
    }

    @Override
    public boolean test(ApplicationFiles applicationFiles) {
        Boolean hidden = applicationFiles.deployProperties().<Boolean>get("hidden");
        return hidden == null || !hidden;
    }
}
