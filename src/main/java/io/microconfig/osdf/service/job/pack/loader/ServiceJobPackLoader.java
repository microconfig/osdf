package io.microconfig.osdf.service.job.pack.loader;

import io.microconfig.osdf.service.job.pack.ServiceJobPack;

import java.util.List;

public interface ServiceJobPackLoader {
    List<ServiceJobPack> loadPacks();
}
