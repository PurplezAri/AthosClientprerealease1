package com.athos.client.modules;

public class BoolSetting extends Setting {
    public boolean value;

    public BoolSetting(String key, String label, boolean value) {
        super(key, label);
        this.value = value;
    }

    @Override public String getType() { return "bool"; }
}
