package io.microconfig.osdf.develop.service.job.pack.loader;

import io.microconfig.osdf.develop.service.job.pack.ServiceJobPack;

import java.util.List;

public interface ServiceJobPackLoader {
    List<ServiceJobPack> loadPacks();
}
