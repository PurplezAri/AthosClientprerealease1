package com.athos.client.hud;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

public class HudCoords extends HudElement {
    public HudCoords() { super("coords", "Coordinates"); }

    @Override public int baseWidth() { return 140; }
    @Override public int baseHeight() { return 10; }

    @Override
    public void render(DrawContext ctx) {
        MinecraftClient c = MinecraftClient.getInstance();
        if (c.player == null) return;

        int px = (int) c.player.getX();
        int py = (int) c.player.getY();
        int pz = (int) c.player.getZ();
        String t = "XYZ: " + px + " " + py + " " + pz;

        ctx.getMatrices().push();
        ctx.getMatrices().translate(x, y, 0);
        ctx.getMatrices().scale(scale, scale, 1);
        ctx.drawText(c.textRenderer, t, 0, 0, 0xFFFFFF, true);
        ctx.getMatrices().pop();
    }
}
