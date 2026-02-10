package com.athos.client.hud;

import com.athos.client.modules.BoolSetting;
import com.athos.client.modules.ColorSetting;
import com.athos.client.modules.Module;
import com.athos.client.modules.ModuleRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

/**
 * Lunar/Feather-style keystrokes HUD (WASD + mouse buttons + CPS) with rounded boxes.
 * Colors are driven by the existing HUD module settings:
 *  - keystrokes.color  (accent for pressed keys)
 *  - keystrokes.shadow (text shadow)
 */
public class HudKeystrokes extends HudElement {
    public HudKeystrokes() { super("keystrokes", "Keystrokes"); }

    // Layout (unscaled)
    private static final int CELL = 22;
    private static final int GAP = 3;
    private static final int RADIUS = 4;

    // Simple CPS counter (LMB)
    private long[] clicks = new long[64];
    private int clickPtr = 0;
    private boolean lastLmb = false;

    @Override public int baseWidth() { return (CELL * 3) + (GAP * 2); }
    @Override public int baseHeight() { return (CELL * 3) + (GAP * 2) + 14; }

    @Override
    public void render(DrawContext ctx) {
        MinecraftClient c = MinecraftClient.getInstance();
        if (c.options == null) return;

        boolean w = c.options.forwardKey.isPressed();
        boolean a = c.options.leftKey.isPressed();
        boolean s = c.options.backKey.isPressed();
        boolean d = c.options.rightKey.isPressed();

        boolean lmb = c.options.attackKey.isPressed();
        boolean rmb = c.options.useKey.isPressed();

        // Track CPS from LMB edges
        long now = System.currentTimeMillis();
        if (lmb && !lastLmb) {
            clicks[clickPtr++ & 63] = now;
        }
        lastLmb = lmb;

        int cps = 0;
        for (long t : clicks) if (t != 0 && now - t <= 1000) cps++;

        // Read styling from module settings (falls back safely)
        int accent = 0xFF59D66A; // green-ish default
        boolean shadow = true;
        Module m = ModuleRegistry.byId("keystrokes");
        if (m != null) {
            if (m.settings.get("color") instanceof ColorSetting cs) accent = parseHex(cs.hex, accent);
            if (m.settings.get("shadow") instanceof BoolSetting bs) shadow = bs.value;
        }

        // Colors
        int bg = 0xAA101010;
        int bgOff = 0xAA181818;
        int pressedBg = (accent & 0x00FFFFFF) | 0xAA000000; // keep alpha, use accent rgb
        int textOn = 0xFFFFFFFF;
        int textOff = 0xFFDDDDDD;

        ctx.getMatrices().push();
        ctx.getMatrices().translate(x, y, 0);
        ctx.getMatrices().scale(scale, scale, 1);

        // Row 1: W centered
        drawKey(ctx, c, CELL + GAP, 0, "W", w, bg, bgOff, pressedBg, textOn, textOff, shadow);

        // Row 2: A S D
        int y2 = CELL + GAP;
        drawKey(ctx, c, 0, y2, "A", a, bg, bgOff, pressedBg, textOn, textOff, shadow);
        drawKey(ctx, c, CELL + GAP, y2, "S", s, bg, bgOff, pressedBg, textOn, textOff, shadow);
        drawKey(ctx, c, (CELL + GAP) * 2, y2, "D", d, bg, bgOff, pressedBg, textOn, textOff, shadow);

        // Row 3: LMB / RMB
        int y3 = (CELL + GAP) * 2;
        drawWideKey(ctx, c, 0, y3, "LMB", lmb, bg, bgOff, pressedBg, textOn, textOff, shadow);
        drawWideKey(ctx, c, CELL + GAP + (CELL / 2), y3, "RMB", rmb, bg, bgOff, pressedBg, textOn, textOff, shadow);

        // CPS row
        int y4 = y3 + CELL + GAP;
        String cpsText = cps + " CPS";
        int wAll = baseWidth();
        drawRounded(ctx, 0, y4, wAll, 14, 0xAA0E0E0E, RADIUS);
        int tx = (wAll - c.textRenderer.getWidth(cpsText)) / 2;
        ctx.drawText(c.textRenderer, cpsText, tx, y4 + 3, 0xFFCCCCCC, shadow);

        ctx.getMatrices().pop();
    }

    private void drawKey(DrawContext ctx, MinecraftClient c, int x, int y, String label,
                         boolean pressed, int bg, int bgOff, int pressedBg,
                         int textOn, int textOff, boolean shadow) {
        int color = pressed ? pressedBg : bgOff;
        drawRounded(ctx, x, y, CELL, CELL, color, RADIUS);
        int tw = c.textRenderer.getWidth(label);
        int tx = x + (CELL - tw) / 2;
        int ty = y + (CELL - 9) / 2;
        ctx.drawText(c.textRenderer, label, tx, ty, pressed ? textOn : textOff, shadow);
    }

    private void drawWideKey(DrawContext ctx, MinecraftClient c, int x, int y, String label,
                             boolean pressed, int bg, int bgOff, int pressedBg,
                             int textOn, int textOff, boolean shadow) {
        int w = CELL + (CELL / 2);
        int color = pressed ? pressedBg : bgOff;
        drawRounded(ctx, x, y, w, CELL, color, RADIUS);
        int tw = c.textRenderer.getWidth(label);
        int tx = x + (w - tw) / 2;
        int ty = y + (CELL - 9) / 2;
        ctx.drawText(c.textRenderer, label, tx, ty, pressed ? textOn : textOff, shadow);
    }

    /**
     * Cheap "rounded" rectangle using 1px corner cuts (looks good at HUD scale).
     */
    private void drawRounded(DrawContext ctx, int x, int y, int w, int h, int argb, int r) {
        // center
        ctx.fill(x + r, y, x + w - r, y + h, argb);
        // left/right
        ctx.fill(x, y + r, x + r, y + h - r, argb);
        ctx.fill(x + w - r, y + r, x + w, y + h - r, argb);
        // corners (pixel-ish)
        ctx.fill(x + 1, y + 1, x + r, y + r, argb);
        ctx.fill(x + w - r, y + 1, x + w - 1, y + r, argb);
        ctx.fill(x + 1, y + h - r, x + r, y + h - 1, argb);
        ctx.fill(x + w - r, y + h - r, x + w - 1, y + h - 1, argb);
    }

    private static int parseHex(String hex, int fallback) {
        if (hex == null) return fallback;
        String h = hex.trim();
        if (h.startsWith("#")) h = h.substring(1);
        if (h.length() == 6) {
            try {
                int rgb = Integer.parseInt(h, 16);
                return 0xFF000000 | rgb;
            } catch (Exception ignored) {
                return fallback;
            }
        }
        return fallback;
    }
}
