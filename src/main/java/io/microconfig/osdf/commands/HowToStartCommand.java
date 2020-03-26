package io.microconfig.osdf.commands;


import static io.microconfig.osdf.utils.FileUtils.readAllFromResource;
import static io.microconfig.utils.Logger.announce;

public class HowToStartCommand {
    public void show() {
        announce(readAllFromResource("howToStart.txt"));
    }
}
