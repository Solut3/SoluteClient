
package com.solut.menu;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public final class AitIntegration {
    private static final String MANAGER =
        "dev.amble.ait.client.tardis.manager.ClientTardisManager";

    private AitIntegration() {}

    public record TardisInfo(UUID id, String description) {}

    public static List<TardisInfo> discover() {
        List<TardisInfo> found = new ArrayList<>();

        try {
            Class<?> managerClass = Class.forName(MANAGER);
            Object manager = managerClass
                .getMethod("getInstance")
                .invoke(null);

            Method forEach = managerClass.getMethod(
                "forEach", Consumer.class
            );

            Consumer<Object> visitor = tardis -> {
                try {
                    Object rawId = tardis.getClass()
                        .getMethod("getUuid")
                        .invoke(tardis);

                    if (!(rawId instanceof UUID id)) return;

                    found.add(new TardisInfo(
                        id,
                        "TARDIS " + id.toString().substring(0, 8)
                    ));
                } catch (ReflectiveOperationException ignored) {
                    // Uma entrada incompatível não interrompe a busca.
                }
            };

            forEach.invoke(manager, visitor);
        } catch (ReflectiveOperationException | LinkageError exception) {
            // O AIT pode não estar instalado ou ainda não estar inicializado.
        }

        return found;
    }

    public static void sendCommand(String command) {
        MinecraftClient client = MinecraftClient.getInstance();

        if (client.player == null || client.getNetworkHandler() == null) {
            return;
        }

        client.getNetworkHandler().sendChatCommand(command);
    }

    public static void notify(String message) {
        MinecraftClient client = MinecraftClient.getInstance();

        if (client.player != null) {
            client.player.sendMessage(
                Text.literal("§d[Solute TARDIS] §f" + message),
                false
            );
        }
    }
}
