package io.microconfig.osdf.utils.mock;

import io.cluster.old.cluster.commandline.CommandLineOutput;

import static io.cluster.old.cluster.commandline.CommandLineOutput.*;

public class DeleteMock implements OCMock {
    public static DeleteMock deleteMock() {
        return new DeleteMock();
    }

    @Override
    public CommandLineOutput execute(String command) {
        return output("ok");
    }

    @Override
    public String pattern() {
        return "oc delete all,configmap -l \"application in \\((.*)\\), projectVersion in \\((.*)\\)\"";
    }
}
