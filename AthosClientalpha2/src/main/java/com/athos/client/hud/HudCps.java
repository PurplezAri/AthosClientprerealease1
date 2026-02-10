package com.athos.client.hud;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

public class HudCps extends HudElement {
    private int clicks;
    private long windowStart = System.currentTimeMillis();
    private int cps;

    public HudCps() { super("cps", "CPS"); }

    @Override public int baseWidth() { return 70; }
    @Override public int baseHeight() { return 10; }

    @Override
    public void render(DrawContext ctx) {
        MinecraftClient c = MinecraftClient.getInstance();
        if (c.player == null) return;

        if (c.options.attackKey.wasPressed()) clicks++;

        long now = System.currentTimeMillis();
        if (now - windowStart >= 1000) {
            cps = clicks;
            clicks = 0;
            windowStart = now;
        }

        String t = "CPS: " + cps;
        ctx.getMatrices().push();
        ctx.getMatrices().translate(x, y, 0);
        ctx.getMatrices().scale(scale, scale, 1);
        ctx.drawText(c.textRenderer, t, 0, 0, 0xFFFFFF, true);
        ctx.getMatrices().pop();
    }
}
