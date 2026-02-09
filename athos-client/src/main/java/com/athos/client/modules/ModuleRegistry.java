package com.athos.client.modules;

import com.athos.client.config.AthosConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ModuleRegistry {
    private static final List<Module> MODULES = new ArrayList<>();

    public static void initFromConfig() {
        MODULES.clear();

        // HUD
        addHud("fps", "FPS", true);
        addHud("coords", "Coordinates", true);
        addHud("cps", "CPS", true);
        addHud("keystrokes", "Keystrokes", true);
        addHud("ping", "Ping", true);
        addHud("clock", "Clock", false);
        addHud("direction", "Direction", false);
        addHud("potions", "Potion Effects", false);

        // Visual / QoL “safe” modules (placeholders for later)
        addSimple("fullbright", "Fullbright (Gamma)", Category.VISUAL, false);
        addSimple("zoom", "Zoom (Client-side)", Category.MECHANIC, false);

        saveBackToConfig();
    }

    private static void addHud(String id, String name, boolean defaultEnabled) {
        Module m = addSimple(id, name, Category.HUD, defaultEnabled);

        m.add(new BoolSetting("shadow", "Text Shadow", true));
        m.add(new ColorSetting("color", "Color (Hex)", "#FFFFFF"));
        m.add(new FloatSetting("scale", "Scale", 1.0f, 0.5f, 3.0f, 0.1f));
    }

    private static Module addSimple(String id, String name, Category cat, boolean defaultEnabled) {
        AthosConfig.ModuleState state = AthosConfig.CONFIG.modules.computeIfAbsent(id, k -> {
            AthosConfig.ModuleState s = new AthosConfig.ModuleState();
            s.enabled = defaultEnabled;
            return s;
        });

        Module m = new Module(id, name, cat, state.enabled);
        MODULES.add(m);
        return m;
    }

    public static List<Module> all() { return MODULES; }

    public static Module byId(String id) {
        for (Module m : MODULES) if (m.id.equals(id)) return m;
        return null;
    }

    public static void saveBackToConfig() {
        for (Module m : MODULES) {
            AthosConfig.ModuleState st = AthosConfig.CONFIG.modules.computeIfAbsent(m.id, k -> new AthosConfig.ModuleState());
            st.enabled = m.isEnabled();

            for (Setting s : m.settings.values()) {
                if (s instanceof BoolSetting bs) st.settings.put(s.key, Boolean.toString(bs.value));
                else if (s instanceof FloatSetting fs) st.settings.put(s.key, Float.toString(fs.value));
                else if (s instanceof ColorSetting cs) st.settings.put(s.key, cs.hex.toUpperCase(Locale.ROOT));
            }
        }
        AthosConfig.save();
    }

    public static void loadSettingsFromConfig(Module m) {
        AthosConfig.ModuleState st = AthosConfig.CONFIG.modules.get(m.id);
        if (st == null) return;

        for (Setting s : m.settings.values()) {
            String v = st.settings.get(s.key);
            if (v == null) continue;

            try {
                if (s instanceof BoolSetting bs) bs.value = Boolean.parseBoolean(v);
                else if (s instanceof FloatSetting fs) fs.value = Float.parseFloat(v);
                else if (s instanceof ColorSetting cs) cs.hex = v;
            } catch (Exception ignored) {}
        }
    }
}
