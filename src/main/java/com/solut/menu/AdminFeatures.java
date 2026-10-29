
package com.solut.menu;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.text.Text;

public final class AdminFeatures {
    private static boolean locatorEnabled = false;
    private static boolean xrayEnabled = false;
    private static boolean invisibilityRequested = false;

    private AdminFeatures() {}

    public static boolean isLocatorEnabled() {
        return locatorEnabled;
    }

    public static boolean isXrayEnabled() {
        return xrayEnabled;
    }

    public static void toggleLocator() {
        locatorEnabled = !locatorEnabled;
    }

    public static void toggleXray() {
        xrayEnabled = !xrayEnabled;
    }
    public static boolean isInvisibilityRequested() {
    return invisibilityRequested;
}

    public static void toggleInvisibility() {
        MinecraftClient client = MinecraftClient.getInstance();

        if (client.player == null || client.getNetworkHandler() == null) {
            return;
        }

        invisibilityRequested = !invisibilityRequested;

        // O servidor verifica se o jogador tem permissão.
        String command = invisibilityRequested
            ? "effect give @s minecraft:invisibility 1000000 0 true"
            : "effect clear @s minecraft:invisibility";

        client.getNetworkHandler().sendChatCommand(command);

        client.player.sendMessage(
            Text.literal(invisibilityRequested
                ? "§d[Solute Admin] Solicitação de invisibilidade enviada."
                : "§d[Solute Admin] Solicitação para remover invisibilidade enviada."),
            true
        );
    }

    public static void registerHud() {
        HudRenderCallback.EVENT.register((context, tickDelta) -> {
            MinecraftClient client = MinecraftClient.getInstance();

            if (!locatorEnabled || client.player == null
                    || client.world == null || client.currentScreen != null) {
                return;
            }

            int x = 10;
            int y = 10;

            context.drawText(
                client.textRenderer,
                "§dSOLUTE • JOGADORES",
                x, y, 0xFFFFFFFF, true
            );
            y += 13;

            int count = 0;

            for (AbstractClientPlayerEntity player : client.world.getPlayers()) {
                if (player == client.player) {
                    continue;
                }

                double distance = client.player.distanceTo(player);
                String line = String.format(
                    "%s  %.0fm  X:%.0f Y:%.0f Z:%.0f",
                    player.getGameProfile().getName(),
                    distance,
                    player.getX(), player.getY(), player.getZ()
                );

                context.drawText(
                    client.textRenderer,
                    line,
                    x, y, 0xFFFFFFFF, true
                );

                y += 12;
                count++;

                if (count >= 10) {
                    break;
                }
            }

            if (count == 0) {
                context.drawText(
                    client.textRenderer,
                    "Nenhum outro jogador carregado.",
                    x, y, 0xFFB56CFF, true
                );
            }
        });
    }
}
