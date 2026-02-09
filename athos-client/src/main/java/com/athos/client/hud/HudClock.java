package com.athos.client.hud;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class HudClock extends HudElement {
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("HH:mm");

    public HudClock() { super("clock", "Clock"); }

    @Override public int baseWidth() { return 90; }
    @Override public int baseHeight() { return 10; }

    @Override
    public void render(DrawContext ctx) {
        MinecraftClient c = MinecraftClient.getInstance();
        String t = "Time: " + LocalTime.now().format(FMT);

        ctx.getMatrices().push();
        ctx.getMatrices().translate(x, y, 0);
        ctx.getMatrices().scale(scale, scale, 1);
        ctx.drawText(c.textRenderer, t, 0, 0, 0xFFFFFF, true);
        ctx.getMatrices().pop();
    }
}
