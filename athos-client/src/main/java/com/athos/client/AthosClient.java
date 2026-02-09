package com.athos.client;

import com.athos.client.config.AthosConfig;
import com.athos.client.hud.HudManager;
import com.athos.client.modules.ModuleRegistry;
import com.athos.client.ui.AthosMenuScreen;
import com.athos.client.ui.HudEditorScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudLayerRegistrationCallback;
import net.fabricmc.fabric.api.client.rendering.v1.IdentifiedLayer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

public class AthosClient implements ClientModInitializer {
    public static final String MOD_ID = "athosclient";

    private static KeyBinding openMenuKey;
    private static KeyBinding openHudEditorKey;

    @Override
    public void onInitializeClient() {
        AthosConfig.load();

        ModuleRegistry.initFromConfig();
        ModuleRegistry.all().forEach(ModuleRegistry::loadSettingsFromConfig);

        HudManager.initFromConfig();

        openMenuKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.athosclient.open_menu",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_RIGHT_SHIFT,
                "category.athosclient"
        ));

        openHudEditorKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.athosclient.open_hud_editor",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_GRAVE_ACCENT,
                "category.athosclient"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (openMenuKey.wasPressed()) MinecraftClient.getInstance().setScreen(new AthosMenuScreen());
            while (openHudEditorKey.wasPressed()) MinecraftClient.getInstance().setScreen(new HudEditorScreen());
        });

        HudLayerRegistrationCallback.EVENT.register(layeredDrawer ->
                layeredDrawer.attachLayerAfter(
                        IdentifiedLayer.MISC_OVERLAYS,
                        Identifier.of(MOD_ID, "athos_hud"),
                        (context, tickCounter) -> HudManager.render(context)
                )
        );
    }
}
