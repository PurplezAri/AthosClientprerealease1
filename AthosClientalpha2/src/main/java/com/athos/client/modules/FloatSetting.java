package com.athos.client.modules;

public class FloatSetting extends Setting {
    public float value, min, max, step;

    public FloatSetting(String key, String label, float value, float min, float max, float step) {
        super(key, label);
        this.value = value;
        this.min = min;
        this.max = max;
        this.step = step;
    }

    @Override public String getType() { return "float"; }
}
