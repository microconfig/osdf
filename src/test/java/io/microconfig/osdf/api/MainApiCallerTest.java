package io.microconfig.osdf.api;

import io.osdf.common.utils.TestContext;
import org.junit.jupiter.api.Test;

import static io.osdf.api.MainApiCaller.mainApi;
import static io.osdf.core.connection.cli.openshift.OpenShiftCli.oc;
import static io.osdf.common.utils.TestContext.defaultContext;
import static java.util.List.of;

class MainApiCallerTest {
    private final TestContext context = defaultContext();

    @Test
    void callSimpleMethod() {
        context.install();
        mainApi(context.getPaths(), oc(context.getPaths())).call(of("help", "help"));
    }
}