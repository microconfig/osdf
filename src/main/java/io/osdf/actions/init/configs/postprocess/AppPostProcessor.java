package io.osdf.actions.init.configs.postprocess;

import io.osdf.actions.init.configs.postprocess.metadata.MetadataCreator;
import io.osdf.actions.init.configs.postprocess.template.ResourceTemplatePostProcessor;
import io.osdf.actions.init.configs.preprocess.ExternalVariablesResolver;
import io.osdf.core.local.component.ComponentDir;
import io.osdf.settings.paths.OsdfPaths;
import lombok.RequiredArgsConstructor;

import static io.osdf.actions.init.configs.postprocess.ConfigMapCreator.configMapCreator;
import static io.osdf.actions.init.configs.postprocess.ResourceSplitter.resourceSplitter;
import static io.osdf.actions.init.configs.postprocess.template.ResourceTemplatePostProcessor.resourceTemplatePostProcessor;
import static io.osdf.actions.init.configs.postprocess.metadata.MetadataCreatorImpl.metadataCreator;
import static io.osdf.actions.init.configs.preprocess.ExternalVariablesResolver.externalVariablesResolver;
import static io.osdf.common.utils.FileUtils.createDirectoryIfNotExists;

@RequiredArgsConstructor
public class AppPostProcessor {
    private final ConfigMapCreator configMapCreator = configMapCreator();
    private final MetadataCreator metadataCreator = metadataCreator();
    private final ResourceSplitter resourceSplitter = resourceSplitter();
    private final ResourceTemplatePostProcessor resourceTemplatePostProcessor = resourceTemplatePostProcessor();
    private final ExternalVariablesResolver externalVariablesResolver;

    public static AppPostProcessor componentPostProcessor(OsdfPaths paths) {
        return new AppPostProcessor(externalVariablesResolver(paths));
    }

    public void process(ComponentDir componentDir) {
        createDirectoryIfNotExists(componentDir.getPath("resources"));
        externalVariablesResolver.resolve(componentDir.root());
        resourceSplitter.splitResources(componentDir);
        configMapCreator.createConfigMaps(componentDir);
        metadataCreator.create(componentDir);
        resourceTemplatePostProcessor.postProcess(componentDir);
    }
}
