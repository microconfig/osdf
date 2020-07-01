package io.osdf.core.service.core.job.pack.loader;

import io.osdf.core.service.core.job.pack.ServiceJobPack;

import java.util.List;

public interface ServiceJobPackLoader {
    List<ServiceJobPack> loadPacks();
}
