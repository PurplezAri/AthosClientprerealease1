package com.athos.client.modules;

import java.util.LinkedHashMap;
import java.util.Map;

public class Module {
    public final String id;
    public final String name;
    public final Category category;

    private boolean enabled;

    public final Map<String, Setting> settings = new LinkedHashMap<>();

    public Module(String id, String name, Category category, boolean enabled) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.enabled = enabled;
    }

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }

    public Module add(Setting s) { settings.put(s.key, s); return this; }
}
