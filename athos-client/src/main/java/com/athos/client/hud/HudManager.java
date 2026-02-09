package com.athos.client.hud;

import com.athos.client.config.AthosConfig;
import com.athos.client.modules.Module;
import com.athos.client.modules.ModuleRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

import java.util.ArrayList;
import java.util.List;

public class HudManager {
    private static final List<HudElement> ELEMENTS = new ArrayList<>();

    public static void initFromConfig() {
        ELEMENTS.clear();

        ELEMENTS.add(new HudFps());
        ELEMENTS.add(new HudCoords());
        ELEMENTS.add(new HudCps());
        ELEMENTS.add(new HudKeystrokes());
        ELEMENTS.add(new HudPing());
        ELEMENTS.add(new HudClock());
        ELEMENTS.add(new HudDirection());
        ELEMENTS.add(new HudPotions());

        for (HudElement e : ELEMENTS) {
            AthosConfig.HudElementState st = AthosConfig.CONFIG.hud.get(e.id);
            if (st != null) {
                e.enabled = st.enabled;
                e.x = st.x;
                e.y = st.y;
                e.scale = st.scale;
            } else {
                AthosConfig.CONFIG.hud.put(e.id, new AthosConfig.HudElementState());
            }
        }
        AthosConfig.save();
    }

    public static List<HudElement> elements() { return ELEMENTS; }

    public static void saveLayout() {
        for (HudElement e : ELEMENTS) {
            AthosConfig.HudElementState st = AthosConfig.CONFIG.hud.computeIfAbsent(e.id, k -> new AthosConfig.HudElementState());
            st.enabled = e.enabled;
            st.x = e.x;
            st.y = e.y;
            st.scale = e.scale;
        }
        AthosConfig.save();
    }

    public static void render(DrawContext ctx) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return;

        for (HudElement e : ELEMENTS) {
            Module m = ModuleRegistry.byId(e.id);
            boolean moduleEnabled = (m == null) || m.isEnabled();

            if (e.enabled && moduleEnabled) e.render(ctx);
        }
    }
}
