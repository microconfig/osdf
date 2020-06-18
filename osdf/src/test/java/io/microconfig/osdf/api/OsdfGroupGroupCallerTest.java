package io.microconfig.osdf.api;

import io.microconfig.osdf.utils.TestContext;
import org.junit.jupiter.api.Test;

import static io.microconfig.osdf.api.MainApiCaller.mainApi;
import static io.cluster.old.cluster.openshift.OpenShiftCli.oc;
import static io.microconfig.osdf.utils.TestContext.defaultContext;
import static java.util.List.of;

class OsdfGroupGroupCallerTest {
    private final TestContext context = defaultContext();

    @Test
    void callSimpleMethod() {
        context.install();
        mainApi(context.getPaths(), oc(context.getPaths())).call(of("help", "help"));
    }
}