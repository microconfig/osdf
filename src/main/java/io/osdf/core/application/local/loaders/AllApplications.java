package io.osdf.core.application.local.loaders;

import io.osdf.core.application.local.ApplicationFiles;

public class AllApplications implements ApplicationMapper<ApplicationFiles> {
    public static AllApplications all() {
        return new AllApplications();
    }

    @Override
    public boolean check(ApplicationFiles files) {
        return true;
    }

    @Override
    public ApplicationFiles map(ApplicationFiles files) {
        return files;
    }
}
