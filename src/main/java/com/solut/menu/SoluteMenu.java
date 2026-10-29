package com.solut.menu;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class SoluteMenu implements ClientModInitializer {

    private static KeyBinding openMenu;

    @Override
    public void onInitializeClient() {
        // Registrar o atalho uma única vez.
        openMenu = KeyBindingHelper.registerKeyBinding(
            new KeyBinding(
                "key.solute_menu.open",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_RIGHT_SHIFT,
                "category.solute_menu"
            )
        );

        // Restaurar configurações salvas.
        HudSettings.load();

        // Registrar cada HUD uma única vez.
        SoluteHud.register();
        KeystrokesHud.register();
        AdminFeatures.registerHud();

        // Abrir ou fechar o menu.
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (openMenu.wasPressed()) {
                if (client.currentScreen instanceof SoluteMenuScreen) {
                    client.setScreen(null);
                } else if (client.currentScreen == null) {
                    client.setScreen(new SoluteMenuScreen());
                }
            }
        });
    }
}