package io.osdf.test.cluster.api;

import io.osdf.core.application.core.description.CoreDescription;
import io.osdf.core.application.core.description.ResourceDescription;
import io.osdf.core.application.job.JobDescription;
import io.osdf.core.connection.cli.CliOutput;
import io.osdf.test.cluster.TestApiExecutor;
import io.osdf.test.cluster.TestCli;
import lombok.RequiredArgsConstructor;
import org.yaml.snakeyaml.Yaml;

import java.util.Map;

import static io.osdf.test.cluster.api.ConfigMapApi.configMapApi;
import static io.osdf.test.cluster.api.JobApi.jobApi;
import static java.util.List.of;

@RequiredArgsConstructor
public class JobAppApi extends TestCli {
    private final ConfigMapApi configMapApi;
    private final JobApi jobApi;

    public static JobAppApi jobAppApi(String name) {
        ConfigMapApi configMapApi = configMapApi(name + "-osdf")
                .setContent(initialContent(name));

        return new JobAppApi(configMapApi, jobApi(name));
    }

    private static Map<String, String> initialContent(String name) {
        CoreDescription coreDescription = new CoreDescription();
        coreDescription.setAppVersion("latest");
        coreDescription.setConfigVersion("master");
        coreDescription.setResources(of("job/" + name));
        String core = new Yaml().dump(coreDescription);

        JobDescription jobDescription = new JobDescription();
        jobDescription.setJob(new ResourceDescription("job", name));
        String job = new Yaml().dump(jobDescription);

        return Map.of("core", core, "job", job);
    }

    @Override
    public CliOutput execute(String command) {
        return TestApiExecutor.builder()
                .executor(configMapApi::execute)
                .executor(jobApi::execute)
                .build().execute(command);
    }
}
