package io.microconfig.osdf.printer;

import org.junit.jupiter.api.Test;

import java.util.function.Consumer;

import static io.microconfig.osdf.printer.ColumnPrinter.printer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class ColumnPrinterTest {
    @Test
    @SuppressWarnings("unchecked")
    void basicTest() {
        Consumer<String> stringConsumer = mock(Consumer.class);

        ColumnPrinter printer = printer("C1", "C2");
        printer.addRow(stringConsumer, "a1", "a2");
        printer.addRow(stringConsumer, "b1", "b2");
        printer.print();

        verify(stringConsumer).accept("a1    a2    ");
        verify(stringConsumer).accept("b1    b2    ");
    }
}