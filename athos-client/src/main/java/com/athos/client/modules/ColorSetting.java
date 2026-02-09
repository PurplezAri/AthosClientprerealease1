package com.athos.client.modules;

public class ColorSetting extends Setting {
    public String hex;

    public ColorSetting(String key, String label, String hex) {
        super(key, label);
        this.hex = hex;
    }

    @Override public String getType() { return "color"; }
}
