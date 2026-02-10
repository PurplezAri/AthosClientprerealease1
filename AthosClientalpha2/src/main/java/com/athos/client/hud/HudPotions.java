package com.athos.client.hud;

import com.athos.client.modules.BoolSetting;
import com.athos.client.modules.ColorSetting;
import com.athos.client.modules.FloatSetting;
import com.athos.client.modules.Module;
import com.athos.client.modules.ModuleRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.text.Text;

public class HudPotions extends HudElement {
    public HudPotions() { super("potions", "Potion Effects"); }

    @Override public int baseWidth() { return 160; }
    @Override public int baseHeight() { return 50; }

    private static int parseHex(String s, int fallback) {
        if (s == null) return fallback;
        String t = s.trim();
        if (t.startsWith("#")) t = t.substring(1);
        if (t.length() == 6) {
            try { return Integer.parseInt(t, 16); } catch (Exception ignored) {}
        }
        return fallback;
    }

    private static String toRoman(int n) {
        // 1..10 (enough for vanilla amplifiers)
        return switch (n) {
            case 1 -> "I";
            case 2 -> "II";
            case 3 -> "III";
            case 4 -> "IV";
            case 5 -> "V";
            case 6 -> "VI";
            case 7 -> "VII";
            case 8 -> "VIII";
            case 9 -> "IX";
            case 10 -> "X";
            default -> Integer.toString(n);
        };
    }

    private static String formatTime(int totalSeconds, boolean mmss) {
        if (!mmss) return totalSeconds + "s";
        int m = Math.max(0, totalSeconds) / 60;
        int s = Math.max(0, totalSeconds) % 60;
        return m + ":" + (s < 10 ? "0" : "") + s;
    }

    @Override
    public void render(DrawContext ctx) {
        MinecraftClient c = MinecraftClient.getInstance();
        if (c.player == null) return;

        Module m = ModuleRegistry.byId("potions");
        // Safe defaults if module not found for any reason
        boolean shadow = true;
        boolean showTier = true;
        boolean mmss = true;
        int color = 0xFFFFFF;
        float moduleScale = 1.0f;

        if (m != null) {
            BoolSetting shadowS = (BoolSetting) m.settings.get("shadow");
            ColorSetting colorS = (ColorSetting) m.settings.get("color");
            FloatSetting scaleS = (FloatSetting) m.settings.get("scale");
            BoolSetting tierS = (BoolSetting) m.settings.get("showTier");
            BoolSetting mmssS = (BoolSetting) m.settings.get("mmss");

            if (shadowS != null) shadow = shadowS.value;
            if (tierS != null) showTier = tierS.value;
            if (mmssS != null) mmss = mmssS.value;
            if (colorS != null) color = parseHex(colorS.hex, 0xFFFFFF);
            if (scaleS != null) moduleScale = scaleS.value;
        }

        ctx.getMatrices().push();
        ctx.getMatrices().translate(x, y, 0);
        ctx.getMatrices().scale(scale * moduleScale, scale * moduleScale, 1);

        int yy = 0;
        int drawn = 0;
        for (StatusEffectInstance e : c.player.getStatusEffects()) {
            Text nameT = e.getEffectType().value().getName();
            String name = nameT.getString();

            int tier = e.getAmplifier() + 1;
            if (showTier) name = name + " " + toRoman(tier);

            int secs = (int) Math.ceil(e.getDuration() / 20.0);
            String t = name + " (" + formatTime(secs, mmss) + ")";

            ctx.drawText(c.textRenderer, t, 0, yy, color, shadow);
            yy += 10;
            drawn++;
            if (yy > 80 || drawn > 8) break;
        }

        if (drawn == 0) {
            ctx.drawText(c.textRenderer, "No effects", 0, 0, color, shadow);
        }

        ctx.getMatrices().pop();
    }
}
