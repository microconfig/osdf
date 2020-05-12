package io.microconfig.osdf.utils.mock;

import io.microconfig.osdf.commandline.CommandLineOutput;

import static io.microconfig.osdf.commandline.CommandLineOutput.*;

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
