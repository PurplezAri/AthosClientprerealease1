package com.athos.client.ui.widgets;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

public class ToggleRow {
    public int x, y, w, h;
    public String label;
    public boolean value;

    public ToggleRow(String label, boolean value) {
        this.label = label;
        this.value = value;
        this.h = 18;
    }

    public void setBounds(int x, int y, int w) {
        this.x = x; this.y = y; this.w = w;
    }

    public void render(DrawContext ctx, int mouseX, int mouseY, boolean shadow) {
        int bg = isHover(mouseX, mouseY) ? 0x33222222 : 0x22000000;
        ctx.fill(x, y, x + w, y + h, bg);

        var tr = MinecraftClient.getInstance().textRenderer;

        ctx.drawText(tr, label, x + 6, y + 5, 0xFFFFFF, shadow);

        int pillW = 40;
        int px = x + w - pillW - 6;
        int py = y + 3;
        int pill = value ? 0xAA1E7F2D : 0xAA7F1E1E;
        ctx.fill(px, py, px + pillW, py + 12, pill);
        ctx.drawText(tr, value ? "ON" : "OFF", px + 12, py + 2, 0xFFFFFF, false);
    }

    public boolean mouseClicked(double mx, double my) {
        if (isHover((int) mx, (int) my)) {
            value = !value;
            return true;
        }
        return false;
    }

    private boolean isHover(int mx, int my) {
        return mx >= x && mx <= x + w && my >= y && my <= y + h;
    }
}
