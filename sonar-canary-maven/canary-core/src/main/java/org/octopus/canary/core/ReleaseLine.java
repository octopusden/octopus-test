package org.octopus.canary.core;

public final class ReleaseLine {

    private final String value;

    public ReleaseLine(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }
}
