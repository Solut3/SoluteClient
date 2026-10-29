
package com.solut.menu;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class HudSettings {

    private static final Gson GSON =
        new GsonBuilder().setPrettyPrinting().create();

    private static final Path CONFIG_FILE =
        FabricLoader.getInstance()
            .getConfigDir()
            .resolve("solute-menu.json");

    public static boolean playerInfo = true;
    public static boolean coordinates = true;
    public static boolean fps = true;
    public static boolean keystrokes = true;

    private HudSettings() {}

    public static void load() {
        if (!Files.exists(CONFIG_FILE)) {
            save();
            return;
        }

        try {
            String json = Files.readString(CONFIG_FILE);
            JsonObject config = JsonParser.parseString(json)
                .getAsJsonObject();

            playerInfo = getBoolean(config, "playerInfo", true);
            coordinates = getBoolean(config, "coordinates", true);
            fps = getBoolean(config, "fps", true);
            keystrokes = getBoolean(config, "keystrokes", true);

        } catch (Exception e) {
            System.err.println(
                "[Solute Menu] Erro ao carregar configurações: "
                    + e.getMessage()
            );
        }
    }

    private static boolean getBoolean(
        JsonObject config,
        String key,
        boolean defaultValue
    ) {
        if (!config.has(key) || config.get(key).isJsonNull()) {
            return defaultValue;
        }

        try {
            return config.get(key).getAsBoolean();
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public static void save() {
        JsonObject config = new JsonObject();

        config.addProperty("playerInfo", playerInfo);
        config.addProperty("coordinates", coordinates);
        config.addProperty("fps", fps);
        config.addProperty("keystrokes", keystrokes);

        try {
            Files.createDirectories(CONFIG_FILE.getParent());
            Files.writeString(CONFIG_FILE, GSON.toJson(config));
        } catch (IOException e) {
            System.err.println(
                "[Solute Menu] Erro ao salvar configurações: "
                    + e.getMessage()
            );
        }
    }

    public static void togglePlayerInfo() {
        playerInfo = !playerInfo;
        save();
    }

    public static void toggleCoordinates() {
        coordinates = !coordinates;
        save();
    }

    public static void toggleFps() {
        fps = !fps;
        save();
    }
    public static void toggleKeystrokes() {
    keystrokes = !keystrokes;
    save();
}
}
