package io.osdf.actions.info.api.status.printer;

import io.osdf.actions.info.api.status.AppStatusRowsGetter;
import io.osdf.actions.info.printer.ColumnPrinter;
import io.osdf.actions.info.status.plain.PlainAppStatus;
import io.osdf.core.application.core.Application;
import io.osdf.core.application.core.description.CoreDescription;
import io.osdf.core.application.plain.PlainApplication;

import java.util.Optional;

import static io.microconfig.utils.ConsoleColor.green;
import static io.microconfig.utils.ConsoleColor.red;
import static io.osdf.actions.info.api.status.printer.RowColumnsWithStatusImpl.rowColumnsWithStatus;
import static io.osdf.actions.info.api.status.printer.StatusRowsFormatter.formatter;
import static io.osdf.actions.info.status.plain.PlainAppStatus.DEPLOYED;
import static io.osdf.actions.info.status.plain.PlainAppStatus.NOT_FOUND;

public class PlainAppStatusRowsGetter implements AppStatusRowsGetter {
    public static PlainAppStatusRowsGetter plainAppStatusRowsGetter() {
        return new PlainAppStatusRowsGetter();
    }

    @Override
    public RowColumnsWithStatus statusOf(Application app, ColumnPrinter printer) {
        PlainApplication plainApp = (PlainApplication) app;
        return rowColumnsWithStatus(printer, addRowsToPrinterAndReturnStatus(plainApp, formatter(printer)));
    }

    private boolean addRowsToPrinterAndReturnStatus(PlainApplication plainApp, StatusRowsFormatter formatter) {
        Optional<CoreDescription> coreDescription = plainApp.coreDescription();
        if (coreDescription.isEmpty()) {
            formatter.addNotFoundRow(plainApp.files(), coloredStatus(NOT_FOUND));
        } else {
            formatter.addMainRow(plainApp.files(), coreDescription.get(), coloredStatus(DEPLOYED), "-");
        }
        return coreDescription.isPresent();
    }

    private String coloredStatus(PlainAppStatus status) {
        String statusString = status.toString().replace("_", " ");
        return status == DEPLOYED ? green(statusString) : red(statusString);
    }
}
