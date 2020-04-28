package io.microconfig.osdf.api.v2.impls;

import io.microconfig.osdf.api.v2.apis.FrequentlyUsedApi;
import io.microconfig.osdf.exceptions.OSDFException;

public class FrequentlyUsedApiImpl implements FrequentlyUsedApi {
    public static FrequentlyUsedApi frequentlyUsedApi() {
        return new FrequentlyUsedApiImpl();
    }

    @Override
    public void group(String group) {
        throw new OSDFException("Not Implemented yet");
    }

    @Override
    public void pull() {
        throw new OSDFException("Not Implemented yet");
    }

    @Override
    public void configVersion(String configVersion) {
        throw new OSDFException("Not Implemented yet");
    }
}
