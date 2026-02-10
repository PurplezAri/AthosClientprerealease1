package com.athos.client.ui;

import com.athos.client.modules.BoolSetting;
import com.athos.client.modules.Category;
import com.athos.client.modules.FloatSetting;
import com.athos.client.modules.Module;
import com.athos.client.modules.ModuleRegistry;
import com.athos.client.modules.Setting;
import com.athos.client.ui.widgets.SliderRow;
import com.athos.client.ui.widgets.ToggleRow;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AthosMenuScreen extends Screen {
    private static final Identifier LOGO = Identifier.of("athosclient", "textures/gui/logo.png");
    private Category selectedCat = Category.HUD;
    private String search = "";
    private Module selectedModule = null;

    private final List<ToggleRow> toggles = new ArrayList<>();
    private final List<SliderRow> sliders = new ArrayList<>();

    public AthosMenuScreen() {
        super(Text.literal("Athos Client"));
    }

    @Override
    protected void init() {
        int left = 18;
        int top = 18;

        int x = left;
        for (Category c : Category.values()) {
            Category cat = c;
            addDrawableChild(ButtonWidget.builder(Text.literal(c.name()), b -> selectedCat = cat)
                    .dimensions(x, top, 88, 20).build());
            x += 92;
        }

        addDrawableChild(ButtonWidget.builder(Text.literal("HUD Editor"), b ->
                MinecraftClient.getInstance().setScreen(new HudEditorScreen()))
                .dimensions(this.width - 110, 18, 92, 20).build());
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        if (chr >= 32 && chr < 127) search += chr;
        return true;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 259 && !search.isEmpty()) { // backspace
            search = search.substring(0, search.length() - 1);
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    private void rebuildSettingsWidgets() {
        toggles.clear();
        sliders.clear();
        if (selectedModule == null) return;

        for (Setting s : selectedModule.settings.values()) {
            if (s instanceof BoolSetting bs) {
                toggles.add(new ToggleRow(s.label, bs.value));
            } else if (s instanceof FloatSetting fs) {
                sliders.add(new SliderRow(s.label, fs.value, fs.min, fs.max));
            }
        }
    }

    private void pushWidgetValuesBackToModule() {
        if (selectedModule == null) return;

        int ti = 0, si = 0;
        for (Setting s : selectedModule.settings.values()) {
            if (s instanceof BoolSetting bs) {
                bs.value = toggles.get(ti++).value;
            } else if (s instanceof FloatSetting fs) {
                fs.value = sliders.get(si++).value;
            }
        }
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        renderBackground(ctx, mouseX, mouseY, delta);

        // Logo (top-left)
        // 32x32 drawn from the bundled logo texture.
        ctx.drawTexture(LOGO, 18, 44, 0, 0, 32, 32, 744, 712);

        ctx.drawText(textRenderer, "Athos Client", 54, 46, 0xFFFFFF, true);
        ctx.drawText(textRenderer, "Search: " + search, 54, 60, 0xAAAAAA, false);

        int startX = 18;
        int startY = 80;
        int cardW = 170;
        int cardH = 42;
        int gap = 10;

        int settingsPanelX = this.width - 260;
        int settingsPanelY = 60;
        int settingsPanelW = 240;
        int settingsPanelH = this.height - 80;

        int i = 0;
        for (Module m : ModuleRegistry.all()) {
            if (m.category != selectedCat) continue;
            if (!search.isEmpty() && !m.name.toLowerCase(Locale.ROOT).contains(search.toLowerCase(Locale.ROOT))) continue;

            int col = i % 3;
            int row = i / 3;
            int x = startX + col * (cardW + gap);
            int y = startY + row * (cardH + gap);

            int bg = m.isEnabled() ? 0xAA1E7F2D : 0xAA7F1E1E;
            if (selectedModule == m) bg = 0xAA2D4F7F;

            ctx.fill(x, y, x + cardW, y + cardH, bg);
            ctx.drawText(textRenderer, m.name, x + 10, y + 10, 0xFFFFFF, true);
            ctx.drawText(textRenderer, m.isEnabled() ? "ENABLED" : "DISABLED", x + 10, y + 24, 0xDDDDDD, false);
            ctx.drawText(textRenderer, "⚙", x + cardW - 18, y + 10, 0xFFFFFF, true);

            if (mouseX >= x && mouseX <= x + cardW && mouseY >= y && mouseY <= y + cardH) {
                ctx.fill(x, y, x + cardW, y + cardH, 0x22000000);
            }

            i++;
        }

        ctx.fill(settingsPanelX, settingsPanelY, settingsPanelX + settingsPanelW, settingsPanelY + settingsPanelH, 0xAA101010);
        ctx.drawText(textRenderer, "Settings", settingsPanelX + 10, settingsPanelY + 10, 0xFFFFFF, true);

        int sy = settingsPanelY + 28;

        if (selectedModule == null) {
            ctx.drawText(textRenderer, "Click ⚙ on a module", settingsPanelX + 10, sy, 0xAAAAAA, false);
        } else {
            ctx.drawText(textRenderer, selectedModule.name, settingsPanelX + 10, sy, 0xFFFFFF, true);
            sy += 16;

            int wx = settingsPanelX + 10;
            int ww = settingsPanelW - 20;

            for (ToggleRow t : toggles) {
                t.setBounds(wx, sy, ww);
                t.render(ctx, mouseX, mouseY, true);
                sy += 22;
            }

            for (SliderRow s : sliders) {
                s.setBounds(wx, sy, ww);
                s.render(ctx, mouseX, mouseY, true);
                sy += 22;
            }

            ctx.drawText(textRenderer, "Tip: drag sliders; click toggles", settingsPanelX + 10,
                    settingsPanelY + settingsPanelH - 16, 0x777777, false);
        }

        super.render(ctx, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int startX = 18, startY = 80, cardW = 170, cardH = 42, gap = 10;

        int i = 0;
        for (Module m : ModuleRegistry.all()) {
            if (m.category != selectedCat) continue;
            if (!search.isEmpty() && !m.name.toLowerCase(Locale.ROOT).contains(search.toLowerCase(Locale.ROOT))) continue;

            int col = i % 3, row = i / 3;
            int x = startX + col * (cardW + gap);
            int y = startY + row * (cardH + gap);

            boolean inside = mouseX >= x && mouseX <= x + cardW && mouseY >= y && mouseY <= y + cardH;
            if (inside) {
                boolean onGear = mouseX >= (x + cardW - 28) && mouseY <= (y + 24);
                if (onGear) {
                    selectedModule = m;
                    rebuildSettingsWidgets();
                    return true;
                }

                m.setEnabled(!m.isEnabled());
                ModuleRegistry.saveBackToConfig();
                return true;
            }
            i++;
        }

        if (selectedModule != null) {
            for (ToggleRow t : toggles) {
                if (t.mouseClicked(mouseX, mouseY)) {
                    pushWidgetValuesBackToModule();
                    ModuleRegistry.saveBackToConfig();
                    return true;
                }
            }
            for (SliderRow s : sliders) {
                if (s.mouseClicked(mouseX, mouseY)) {
                    pushWidgetValuesBackToModule();
                    ModuleRegistry.saveBackToConfig();
                    return true;
                }
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dx, double dy) {
        if (selectedModule != null) {
            for (SliderRow s : sliders) {
                if (s.mouseDragged(mouseX, mouseY)) {
                    pushWidgetValuesBackToModule();
                    ModuleRegistry.saveBackToConfig();
                    return true;
                }
            }
        }
        return super.mouseDragged(mouseX, mouseY, button, dx, dy);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        for (SliderRow s : sliders) s.mouseReleased();
        return super.mouseReleased(mouseX, mouseY, button);
    }
}
