package com.athos.client.ui.widgets;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

public class SliderRow {
    public int x, y, w, h = 18;
    public String label;
    public float value, min, max;

    private boolean dragging = false;

    public SliderRow(String label, float value, float min, float max) {
        this.label = label;
        this.value = value;
        this.min = min;
        this.max = max;
    }

    public void setBounds(int x, int y, int w) {
        this.x = x; this.y = y; this.w = w;
    }

    public void render(DrawContext ctx, int mouseX, int mouseY, boolean shadow) {
        int bg = isHover(mouseX, mouseY) ? 0x33222222 : 0x22000000;
        ctx.fill(x, y, x + w, y + h, bg);

        var tr = MinecraftClient.getInstance().textRenderer;
        ctx.drawText(tr, label, x + 6, y + 5, 0xFFFFFF, shadow);

        int barX = x + w - 110;
        int barY = y + 7;
        int barW = 90;
        ctx.fill(barX, barY, barX + barW, barY + 4, 0x66000000);

        float t = (value - min) / (max - min);
        if (t < 0) t = 0;
        if (t > 1) t = 1;

        int knobX = barX + (int)(t * barW);
        ctx.fill(knobX - 2, barY - 2, knobX + 2, barY + 6, 0xAAFFFFFF);

        String v = String.format(java.util.Locale.ROOT, "%.2f", value);
        ctx.drawText(tr, v, barX - 34, y + 5, 0xDDDDDD, false);
    }

    public boolean mouseClicked(double mx, double my) {
        if (isHover((int) mx, (int) my)) {
            dragging = true;
            updateFromMouse(mx);
            return true;
        }
        return false;
    }

    public void mouseReleased() {
        dragging = false;
    }

    public boolean mouseDragged(double mx, double my) {
        if (dragging) {
            updateFromMouse(mx);
            return true;
        }
        return false;
    }

    private void updateFromMouse(double mx) {
        int barX = x + w - 110;
        int barW = 90;
        float t = (float)((mx - barX) / (double)barW);
        if (t < 0) t = 0;
        if (t > 1) t = 1;
        value = min + t * (max - min);
    }

    private boolean isHover(int mx, int my) {
        return mx >= x && mx <= x + w && my >= y && my <= y + h;
    }
}
