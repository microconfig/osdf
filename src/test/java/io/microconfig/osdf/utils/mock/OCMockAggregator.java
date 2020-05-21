package io.microconfig.osdf.utils.mock;

import io.microconfig.osdf.commandline.CommandLineOutput;
import io.microconfig.osdf.openshift.OpenShiftCLI;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static io.microconfig.osdf.commandline.CommandLineOutput.output;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RequiredArgsConstructor
public class OCMockAggregator {
    private final List<OCMock> mocks;

    public static OpenShiftCLI createMock(List<OCMock> mocks) {
        OCMockAggregator aggregator = new OCMockAggregator(mocks);
        OpenShiftCLI oc = mock(OpenShiftCLI.class);
        when(oc.execute(anyString())).thenAnswer(invocation -> aggregator.execute(invocation.getArgument(0)));
        return oc;
    }

    public CommandLineOutput execute(String command) {
        for (OCMock mock : mocks) {
            if (mock.testCommand(command)) return mock.execute(command);
        }
        return output("empty response");
    }
}
