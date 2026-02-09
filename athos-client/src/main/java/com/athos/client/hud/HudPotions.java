package com.athos.client.hud;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.text.Text;

public class HudPotions extends HudElement {
    public HudPotions() { super("potions", "Potion Effects"); }

    @Override public int baseWidth() { return 160; }
    @Override public int baseHeight() { return 50; }

    @Override
    public void render(DrawContext ctx) {
        MinecraftClient c = MinecraftClient.getInstance();
        if (c.player == null) return;

        ctx.getMatrices().push();
        ctx.getMatrices().translate(x, y, 0);
        ctx.getMatrices().scale(scale, scale, 1);

        int yy = 0;
        for (StatusEffectInstance e : c.player.getStatusEffects()) {
            Text name = e.getEffectType().value().getName();
            int secs = e.getDuration() / 20;
            String t = name.getString() + " (" + secs + "s)";
            ctx.drawText(c.textRenderer, t, 0, yy, 0xFFFFFF, true);
            yy += 10;
            if (yy > 80) break;
        }
        if (yy == 0) ctx.drawText(c.textRenderer, "No effects", 0, 0, 0xFFFFFF, true);

        ctx.getMatrices().pop();
    }
}
