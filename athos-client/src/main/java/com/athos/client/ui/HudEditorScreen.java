package com.athos.client.ui;

import com.athos.client.hud.HudElement;
import com.athos.client.hud.HudManager;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

public class HudEditorScreen extends Screen {
    private HudElement selected;
    private int dragOffX, dragOffY;

    private static final int GRID = 4;

    public HudEditorScreen() {
        super(Text.literal("Athos HUD Editor"));
    }

    @Override
    public void close() {
        HudManager.saveLayout();
        super.close();
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        renderBackground(ctx);

        int cx = this.width / 2;
        int cy = this.height / 2;

        // center guides
        ctx.fill(cx - 1, 0, cx + 1, this.height, 0x22FFFFFF);
        ctx.fill(0, cy - 1, this.width, cy + 1, 0x22FFFFFF);

        ctx.drawText(textRenderer,
                "HUD Editor: drag | scroll=scale | right-click=toggle | Alt=no-snap | R=reset",
                12, 12, 0xFFFFFF, true);

        for (HudElement e : HudManager.elements()) {
            if (e.enabled) {
                e.render(ctx);
            }

            int w = (int) (e.baseWidth() * e.scale);
            int h = (int) (e.baseHeight() * e.scale);

            int x1 = e.x - 2, y1 = e.y - 2, x2 = e.x + w + 2, y2 = e.y + h + 2;

            int outline = (e == selected) ? 0xAAFFFFFF : 0x66FFFFFF;
            int fill = e.enabled ? 0x11000000 : 0x22111111;

            ctx.fill(x1, y1, x2, y2, fill);
            ctx.drawBorder(x1, y1, x2 - x1, y2 - y1, outline);

            ctx.drawText(textRenderer, e.name + (e.enabled ? "" : " (OFF)"), x1, y1 - 10, 0xDDDDDD, false);
        }

        super.render(ctx, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        var elements = HudManager.elements();
        for (int i = elements.size() - 1; i >= 0; i--) {
            HudElement e = elements.get(i);

            int w = (int) (e.baseWidth() * e.scale);
            int h = (int) (e.baseHeight() * e.scale);

            if (mouseX >= e.x && mouseX <= e.x + w && mouseY >= e.y && mouseY <= e.y + h) {
                selected = e;
                dragOffX = (int) mouseX - e.x;
                dragOffY = (int) mouseY - e.y;

                if (button == 1) {
                    e.enabled = !e.enabled;
                    HudManager.saveLayout();
                }
                return true;
            }
        }
        selected = null;
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dx, double dy) {
        if (selected != null && button == 0) {
            boolean snap = !Screen.hasAltDown();

            int nx = (int) mouseX - dragOffX;
            int ny = (int) mouseY - dragOffY;

            if (snap) {
                nx = (nx / GRID) * GRID;
                ny = (ny / GRID) * GRID;
            }

            selected.x = nx;
            selected.y = ny;

            HudManager.saveLayout();
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, dx, dy);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (selected != null) {
            float s = selected.scale + (float) (verticalAmount * 0.05);
            if (s < 0.5f) s = 0.5f;
            if (s > 3.0f) s = 3.0f;
            selected.scale = s;

            HudManager.saveLayout();
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (selected != null && keyCode == GLFW.GLFW_KEY_R) {
            selected.x = 5;
            selected.y = 5;
            selected.scale = 1.0f;
            HudManager.saveLayout();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
}
