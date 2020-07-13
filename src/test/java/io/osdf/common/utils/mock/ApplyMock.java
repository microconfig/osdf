package io.osdf.common.utils.mock;

import io.osdf.core.connection.cli.CliOutput;

import static io.osdf.core.connection.cli.CliOutput.output;


public class ApplyMock implements OCMock {
    public static ApplyMock applyMock() {
        return new ApplyMock();
    }

    @Override
    public CliOutput execute(String command) {
        return output("ok");
    }

    @Override
    public String pattern() {
        return "oc apply -f (.*)";
    }
}
