package io.osdf.common.utils.mock;

import io.osdf.core.connection.cli.CliOutput;
import io.osdf.core.connection.cli.openshift.OpenShiftCli;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static io.osdf.core.connection.cli.CliOutput.output;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RequiredArgsConstructor
public class OCMockAggregator {
    private final List<OCMock> mocks;

    public static OpenShiftCli createMock(List<OCMock> mocks) {
        OCMockAggregator aggregator = new OCMockAggregator(mocks);
        OpenShiftCli oc = mock(OpenShiftCli.class);
        when(oc.execute(anyString())).thenAnswer(invocation -> aggregator.execute(invocation.getArgument(0)));
        return oc;
    }

    public CliOutput execute(String command) {
        for (OCMock mock : mocks) {
            if (mock.testCommand(command)) return mock.execute(command);
        }
        return output("empty response");
    }
}
