package io.osdf.actions.management.clearapps;

import io.osdf.common.exceptions.OSDFException;
import io.osdf.core.connection.cli.ClusterCli;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Map;

import static io.osdf.core.cluster.configmap.ConfigMapLoader.configMapLoader;
import static java.util.Collections.emptyList;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AppList {
    private List<String> apps;

    public static AppList remoteAppList(ClusterCli cli) {
        try {
            return configMapLoader(cli).load("osdf-apps", "apps", AppList.class);
        } catch (OSDFException e) {
            return new AppList(emptyList());
        }
    }

    public static AppList appList(List<String> apps) {
        return new AppList(apps);
    }

    public void upload(ClusterCli cli) {
        configMapLoader(cli)
                .upload("osdf-apps", Map.of("apps", this));
    }
}
