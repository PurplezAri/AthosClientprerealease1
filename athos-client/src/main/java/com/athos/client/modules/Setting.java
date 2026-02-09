package com.athos.client.modules;

public abstract class Setting {
    public final String key;
    public final String label;

    protected Setting(String key, String label) {
        this.key = key;
        this.label = label;
    }

    public abstract String getType(); // "bool", "float", "color"
}
