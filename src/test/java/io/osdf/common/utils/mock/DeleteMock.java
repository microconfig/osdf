package io.osdf.common.utils.mock;

import io.osdf.core.connection.cli.CliOutput;

import static io.osdf.core.connection.cli.CliOutput.*;

public class DeleteMock implements OCMock {
    public static DeleteMock deleteMock() {
        return new DeleteMock();
    }

    @Override
    public CliOutput execute(String command) {
        return output("ok");
    }

    @Override
    public String pattern() {
        return "oc delete all,configmap -l \"application in \\((.*)\\), projectVersion in \\((.*)\\)\"";
    }
}
