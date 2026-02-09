package com.athos.client.hud;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

public class HudFps extends HudElement {
    public HudFps() { super("fps", "FPS"); }

    @Override public int baseWidth() { return 70; }
    @Override public int baseHeight() { return 10; }

    @Override
    public void render(DrawContext ctx) {
        MinecraftClient c = MinecraftClient.getInstance();
        String t = "FPS: " + c.getCurrentFps();
        ctx.getMatrices().push();
        ctx.getMatrices().translate(x, y, 0);
        ctx.getMatrices().scale(scale, scale, 1);
        ctx.drawText(c.textRenderer, t, 0, 0, 0xFFFFFF, true);
        ctx.getMatrices().pop();
    }
}
