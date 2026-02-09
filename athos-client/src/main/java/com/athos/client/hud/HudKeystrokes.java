package com.athos.client.hud;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

public class HudKeystrokes extends HudElement {
    public HudKeystrokes() { super("keystrokes", "Keystrokes"); }

    @Override public int baseWidth() { return 70; }
    @Override public int baseHeight() { return 22; }

    @Override
    public void render(DrawContext ctx) {
        MinecraftClient c = MinecraftClient.getInstance();
        if (c.options == null) return;

        boolean w = c.options.forwardKey.isPressed();
        boolean a = c.options.leftKey.isPressed();
        boolean s = c.options.backKey.isPressed();
        boolean d = c.options.rightKey.isPressed();

        ctx.getMatrices().push();
        ctx.getMatrices().translate(x, y, 0);
        ctx.getMatrices().scale(scale, scale, 1);

        ctx.drawText(c.textRenderer, w ? "[W]" : " W ", 16, 0, 0xFFFFFF, true);
        ctx.drawText(c.textRenderer, (a ? "[A]" : " A ") + (s ? "[S]" : " S ") + (d ? "[D]" : " D "), 0, 12, 0xFFFFFF, true);

        ctx.getMatrices().pop();
    }
}
