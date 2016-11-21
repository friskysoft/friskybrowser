package com.friskysoft.framework;

public enum ArchType {
    X86(32), X64(64);

    ArchType(int value) {
        this.value = value;
    }

    int value;

    public int getValue() {
        return value;
    }
}
