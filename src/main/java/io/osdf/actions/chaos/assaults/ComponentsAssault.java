package io.osdf.actions.chaos.assaults;

import io.osdf.actions.chaos.ChaosContext;
import io.osdf.actions.chaos.events.EventSender;
import io.osdf.actions.chaos.state.AssaultInfoManager;
import io.osdf.actions.management.deploy.deployer.plain.PlainAppDeployer;
import io.osdf.core.application.plain.PlainApplication;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static io.osdf.actions.chaos.events.EventLevel.CHAOS;
import static io.osdf.actions.management.deploy.deployer.plain.PlainAppDeployer.plainAppDeployer;
import static io.osdf.core.application.core.files.loaders.ApplicationFilesLoaderImpl.appLoader;
import static io.osdf.core.application.core.files.loaders.filters.RequiredComponentsFilter.requiredComponentsFilter;
import static io.osdf.core.application.plain.PlainApplicationMapper.plain;
import static java.util.Map.of;
import static java.util.stream.Collectors.joining;

@RequiredArgsConstructor
public class ComponentsAssault implements Assault {
    private static final String ASSAULT_NAME = "components";

    private final PlainAppDeployer deployer;
    private final List<PlainApplication> apps;
    private final AssaultInfoManager assaultInfoManager;
    private final EventSender events;

    @SuppressWarnings("unchecked")
    public static ComponentsAssault componentsAssault(Object description, ChaosContext chaosContext) {
        List<PlainApplication> apps = appLoader(chaosContext.paths())
                .withDirFilter(requiredComponentsFilter((List<String>) description))
                .load(plain(chaosContext.cli()));
        return new ComponentsAssault(
                plainAppDeployer(chaosContext.cli()),
                apps,
                chaosContext.chaosStateManager().assaultInfoManager(),
                chaosContext.eventStorage().sender("components assault")
        );
    }

    @Override
    public void start() {
        apps.forEach(app -> {
            deployer.deploy(app);
            events.send(app.name() + " deployed", CHAOS, app.name());
        });
        String appNames = apps.stream().map(PlainApplication::name).collect(joining(","));
        assaultInfoManager.save(new ActiveAssaultInfo(
                ASSAULT_NAME,
                "Deployed " + appNames,
                of("apps", appNames)
        ));
    }

    @Override
    public void stop() {
        apps.forEach(istioApp -> {
            istioApp.delete();
            events.send(istioApp.name() + " deleted", CHAOS, istioApp.name());
        });
        assaultInfoManager.delete(ASSAULT_NAME);
    }

    @Override
    public void clear() {
        ActiveAssaultInfo assaultInfo = assaultInfoManager.get(ASSAULT_NAME);
        if (assaultInfo == null) return;
        stop();
    }
}
