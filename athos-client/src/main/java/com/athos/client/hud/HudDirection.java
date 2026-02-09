package com.athos.client.hud;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

public class HudDirection extends HudElement {
    public HudDirection() { super("direction", "Direction"); }

    @Override public int baseWidth() { return 90; }
    @Override public int baseHeight() { return 10; }

    @Override
    public void render(DrawContext ctx) {
        MinecraftClient c = MinecraftClient.getInstance();
        if (c.player == null) return;

        float yaw = c.player.getYaw() % 360;
        if (yaw < 0) yaw += 360;

        String dir;
        if (yaw >= 315 || yaw < 45) dir = "South";
        else if (yaw < 135) dir = "West";
        else if (yaw < 225) dir = "North";
        else dir = "East";

        String t = "Dir: " + dir;

        ctx.getMatrices().push();
        ctx.getMatrices().translate(x, y, 0);
        ctx.getMatrices().scale(scale, scale, 1);
        ctx.drawText(c.textRenderer, t, 0, 0, 0xFFFFFF, true);
        ctx.getMatrices().pop();
    }
}
