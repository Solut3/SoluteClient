package com.solut.menu;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public final class SoluteHud {

    private static final int PURPLE = 0xFFB56CFF;
    private static final int PANEL = 0xB510111B;
    private static final int TEXT = 0xFFFFFFFF;
    private static final int MUTED = 0xFFBDB4D0;

    private SoluteHud() {}

    public static void register() {
        HudRenderCallback.EVENT.register(
            (context, tickDelta) -> render(context)
        );
    }

    private static void render(DrawContext context) {
        MinecraftClient client = MinecraftClient.getInstance();
        ClientPlayerEntity player = client.player;

        // Não desenhar no menu principal ou sem jogador.
        if (player == null || client.world == null) {
            return;
        }

        List<Text> lines = new ArrayList<>();

        lines.add(Text.literal("SOLUTE HUD")
            .styled(style -> style.withColor(PURPLE)));

        if (HudSettings.playerInfo) {
            lines.add(Text.literal(
                "Jogador: " + player.getName().getString()
            ));

            lines.add(Text.literal(
                "Dimensao: " +
                client.world.getRegistryKey().getValue()
            ));
        }

        if (HudSettings.coordinates) {
            lines.add(Text.literal(String.format(
                java.util.Locale.ROOT,
                "XYZ: %.1f / %.1f / %.1f",
                player.getX(),
                player.getY(),
                player.getZ()
            )));

            lines.add(Text.literal(
                "Direcao: " +
                player.getHorizontalFacing().asString()
            ));
        }

        if (HudSettings.fps) {
            lines.add(Text.literal(
                "FPS: " + client.getCurrentFps()
            ).styled(style -> style.withColor(PURPLE)));
        }

        int x = 8;
        int y = 8;
        int padding = 7;
        int lineHeight = 12;
        int panelWidth = 0;

        for (Text line : lines) {
            panelWidth = Math.max(
                panelWidth,
                client.textRenderer.getWidth(line)
            );
        }

        panelWidth += padding * 2;

        int panelHeight = padding * 2
            + lines.size() * lineHeight;

        // Fundo escuro.
        context.fill(
            x, y,
            x + panelWidth, y + panelHeight,
            PANEL
        );

        // Faixa roxa neon.
        context.fill(
            x, y,
            x + panelWidth, y + 2,
            PURPLE
        );

        int textY = y + padding + 1;

        for (int i = 0; i < lines.size(); i++) {
            int color = i == 0 ? PURPLE : TEXT;

            context.drawText(
                client.textRenderer,
                lines.get(i),
                x + padding,
                textY,
                color,
                false
            );

            textY += lineHeight;
        }
    }
}