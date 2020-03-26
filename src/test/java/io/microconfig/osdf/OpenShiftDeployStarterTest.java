package io.microconfig.osdf;

import org.junit.jupiter.api.Test;


class OpenShiftDeployStarterTest {
    @Test
    void testSimpleCall() {
        OpenShiftDeployStarter.main(new String[]{"help", "help"});
    }
}