package io.microconfig.osdf.printers;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static io.microconfig.osdf.printers.ColumnPrinter.printer;
import static io.microconfig.utils.ConsoleColor.green;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ColumnPrinterTest {
    @Test
    void basicTest() {

        ByteArrayOutputStream printContent = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(printContent);
        System.setOut(printStream);

        ColumnPrinter printer = printer();
        printer.addColumns("C1", "C2");

        printer.addRow("a1", "a2");
        printer.addRow("b1", "b2");
        printer.print();

        assertEquals(green("C1    C2    ") + "\na1    a2    \nb1    b2    \n", printContent.toString());
    }
}