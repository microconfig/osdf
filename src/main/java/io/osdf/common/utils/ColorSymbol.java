package io.osdf.common.utils;

public enum ColorSymbol {
    RESET("\u001B[0m"),
    RED("\u001B[31m"),
    GREEN("\u001B[32m"),
    YELLOW("\u001B[33m");

    private final String str;

    ColorSymbol(String str) {
        this.str = str;
    }

    public String asString() {
        return str;
    }
}
