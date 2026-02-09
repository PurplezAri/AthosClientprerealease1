package com.athos.client.hud;

import net.minecraft.client.gui.DrawContext;

public abstract class HudElement {
    public final String id;
    public final String name;

    public int x, y;
    public float scale;
    public boolean enabled;

    protected HudElement(String id, String name) {
        this.id = id;
        this.name = name;
        this.x = 5;
        this.y = 5;
        this.scale = 1.0f;
        this.enabled = true;
    }

    public abstract int baseWidth();
    public abstract int baseHeight();

    public abstract void render(DrawContext ctx);
}
