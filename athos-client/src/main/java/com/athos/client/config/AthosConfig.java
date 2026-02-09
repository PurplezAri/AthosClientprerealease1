package com.athos.client.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class AthosConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path PATH = FabricLoader.getInstance().getConfigDir().resolve("athosclient.json");

    public static Config CONFIG = new Config();

    public static class HudElementState {
        public boolean enabled = true;
        public int x = 5;
        public int y = 5;
        public float scale = 1.0f;
    }

    public static class ModuleState {
        public boolean enabled = false;
        public Map<String, String> settings = new HashMap<>();
    }

    public static class Config {
        public Map<String, ModuleState> modules = new HashMap<>();
        public Map<String, HudElementState> hud = new HashMap<>();
    }

    public static void load() {
        if (!Files.exists(PATH)) { save(); return; }
        try {
            String json = Files.readString(PATH);
            Config c = GSON.fromJson(json, Config.class);
            CONFIG = (c != null) ? c : new Config();
        } catch (Exception e) {
            CONFIG = new Config();
        }
    }

    public static void save() {
        try {
            Files.createDirectories(PATH.getParent());
            Files.writeString(PATH, GSON.toJson(CONFIG));
        } catch (IOException ignored) {}
    }
}
