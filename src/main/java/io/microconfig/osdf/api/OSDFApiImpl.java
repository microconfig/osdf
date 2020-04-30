package io.microconfig.osdf.api;

import io.microconfig.osdf.commands.*;
import io.microconfig.osdf.common.Credentials;
import io.microconfig.osdf.configs.ConfigsSource;
import io.microconfig.osdf.istio.rulesetters.RoutingRuleSetter;
import io.microconfig.osdf.nexus.NexusArtifact;
import io.microconfig.osdf.openshift.OCExecutor;
import io.microconfig.osdf.openshift.OpenShiftCredentials;
import io.microconfig.osdf.paths.OSDFPaths;
import lombok.RequiredArgsConstructor;

import java.nio.file.Path;
import java.util.List;

import static io.microconfig.osdf.api.OSDFApiInfo.printHelpForMethod;
import static io.microconfig.osdf.istio.rulesetters.HeaderRuleSetter.headerRule;
import static io.microconfig.osdf.istio.rulesetters.MirrorRuleSetter.mirrorRule;
import static io.microconfig.osdf.istio.rulesetters.WeightRuleSetter.weightRule;
import static io.microconfig.osdf.printers.ColumnPrinter.printer;
import static java.util.List.of;

@RequiredArgsConstructor
public class OSDFApiImpl implements OSDFApi {
    private final OSDFPaths paths;
    private final OCExecutor oc;

    public static OSDFApi osdfApi(OSDFPaths paths, OCExecutor oc) {
        return new OSDFApiImpl(paths, oc);
    }

    @Override
    public void install(Boolean noBashRc, Boolean clearState) {
    }

    @Override
    public void init(String gitUrl, String nexusUrl, NexusArtifact configsNexusArtifact, Path localConfigs, ConfigsSource configsSource, OpenShiftCredentials openShiftCredentials, Credentials nexusCredentials,
                     String env, String configVersion, String group, String projVersion, List<String> components) {
    }

    @Override
    public void deploy(List<String> components, String mode, Boolean wait) {
        new DeployCommand(paths, oc, null, wait).run(components);
    }

    @Override
    public void route(String componentName, String rule) {
        new RouteCommand(paths, oc, routingRules(oc)).set(componentName, rule);
    }

    @Override
    public void status(List<String> components, Boolean withHealthcheck) {
        new StatusCommand(paths, oc, printer(), withHealthcheck).run(components);
    }

    @Override
    public void restart(List<String> components) {
        new RestartCommand(paths, oc).run(components);
    }

    @Override
    public void stop(List<String> components) {
        new StopCommand(paths, oc).run(components);
    }

    @Override
    public void delete(List<String> components) {
        new DeleteCommand(paths, oc).delete(components);
    }

    @Override
    public void state() {
    }

    @Override
    public void pods(List<String> components) {
        new PodsCommand(paths, oc, printer()).show(components);
    }

    @Override
    public void deletePod(String component, List<String> pods) {
        new DeletePodCommand(paths, oc).delete(component, pods);
    }

    @Override
    public void logs(String component, String pod) {
        new LogsCommand(paths, oc).show(component, pod);
    }

    @Override
    public void propertiesDiff(List<String> components) {
        new PropertiesDiffCommand(paths).show(components);
    }

    @Override
    public void update() {
    }

    @Override
    public void help(String command) {
        printHelpForMethod(command);
    }

    @Override
    public void howToStart() {
        new HowToStartCommand().show();
    }

    private List<RoutingRuleSetter> routingRules(OCExecutor oc) {
        return of(
                weightRule(oc),
                mirrorRule(oc),
                headerRule(oc)
        );
    }
}
