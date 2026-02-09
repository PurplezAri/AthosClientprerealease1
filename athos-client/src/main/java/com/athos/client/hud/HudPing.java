package com.athos.client.hud;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

public class HudPing extends HudElement {
    public HudPing() { super("ping", "Ping"); }

    @Override public int baseWidth() { return 90; }
    @Override public int baseHeight() { return 10; }

    @Override
    public void render(DrawContext ctx) {
        MinecraftClient c = MinecraftClient.getInstance();
        int ping = -1;
        if (c.getNetworkHandler() != null && c.player != null) {
            var entry = c.getNetworkHandler().getPlayerListEntry(c.player.getUuid());
            if (entry != null) ping = entry.getLatency();
        }

        String t = (ping >= 0) ? ("Ping: " + ping + "ms") : "Ping: --";
        ctx.getMatrices().push();
        ctx.getMatrices().translate(x, y, 0);
        ctx.getMatrices().scale(scale, scale, 1);
        ctx.drawText(c.textRenderer, t, 0, 0, 0xFFFFFF, true);
        ctx.getMatrices().pop();
    }
}
