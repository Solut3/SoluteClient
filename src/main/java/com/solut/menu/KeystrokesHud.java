
package com.solut.menu;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.option.GameOptions;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayDeque;
import java.util.Deque;

public final class KeystrokesHud {

    private static final int PURPLE = 0xFFB56CFF;
    private static final int ACTIVE = 0xFF8B42D6;
    private static final int INACTIVE = 0xD91B1B29;
    private static final int WHITE = 0xFFFFFFFF;

    private static boolean previousLeftMouse;
    private static boolean previousRightMouse;

    private static final Deque<Long> leftClicks = new ArrayDeque<>();
    private static final Deque<Long> rightClicks = new ArrayDeque<>();

    private KeystrokesHud() {}

    public static void register() {
        HudRenderCallback.EVENT.register(
            (context, tickDelta) -> render(context)
        );
    }

    private static void render(DrawContext context) {
        MinecraftClient client = MinecraftClient.getInstance();

        if (!HudSettings.keystrokes
                || client.player == null
                || client.world == null
                || client.currentScreen != null) {
            return;
        }

        long window = client.getWindow().getHandle();
        GameOptions options = client.options;

        boolean lmb = GLFW.glfwGetMouseButton(
            window, GLFW.GLFW_MOUSE_BUTTON_1
        ) == GLFW.GLFW_PRESS;

        boolean rmb = GLFW.glfwGetMouseButton(
            window, GLFW.GLFW_MOUSE_BUTTON_2
        ) == GLFW.GLFW_PRESS;

        long now = System.currentTimeMillis();

        // Contabiliza a transição de solto para pressionado.
        if (lmb && !previousLeftMouse) {
            leftClicks.addLast(now);
        }

        if (rmb && !previousRightMouse) {
            rightClicks.addLast(now);
        }

        previousLeftMouse = lmb;
        previousRightMouse = rmb;

        removeOldClicks(leftClicks, now);
        removeOldClicks(rightClicks, now);

        int left = 10;
        int top = context.getScaledWindowHeight() - 126;

        // Painel de fundo e detalhe neon.
        context.fill(left, top, left + 154, top + 116, 0xB910111B);
        context.fill(left, top, left + 154, top + 2, PURPLE);

        context.drawText(
            client.textRenderer,
            "KEYSTROKES",
            left + 6, top + 5, PURPLE, false
        );

        int keyY = top + 23;

        // WASD
        drawKey(context, client, left + 42, keyY,
            28, 22, "W", options.forwardKey.isPressed());

        drawKey(context, client, left + 10, keyY + 25,
            28, 22, "A", options.leftKey.isPressed());

        drawKey(context, client, left + 42, keyY + 25,
            28, 22, "S", options.backKey.isPressed());

        drawKey(context, client, left + 74, keyY + 25,
            28, 22, "D", options.rightKey.isPressed());

        // Espaço, agachar e correr.
        drawKey(context, client, left + 10, keyY + 50,
            48, 18, "SHIFT", options.sneakKey.isPressed());

        drawKey(context, client, left + 62, keyY + 50,
            40, 18, "CTRL", options.sprintKey.isPressed());

        drawKey(context, client, left + 10, keyY + 71,
            92, 18, "SPACE", options.jumpKey.isPressed());

        // Botões do mouse e CPS.
        drawKey(context, client, left + 108, keyY,
            40, 30, "LMB", lmb);

        drawKey(context, client, left + 108, keyY + 34,
            40, 30, "RMB", rmb);

        context.drawText(
            client.textRenderer,
            "CPS " + leftClicks.size(),
            left + 108, keyY + 68, WHITE, false
        );

        context.drawText(
            client.textRenderer,
            "CPS " + rightClicks.size(),
            left + 108, keyY + 80, WHITE, false
        );
    }

    private static void removeOldClicks(
        Deque<Long> clicks,
        long now
    ) {
        while (!clicks.isEmpty()
                && now - clicks.peekFirst() >= 1000) {
            clicks.removeFirst();
        }
    }

    private static void drawKey(
        DrawContext context,
        MinecraftClient client,
        int x,
        int y,
        int width,
        int height,
        String label,
        boolean pressed
    ) {
        int background = pressed ? ACTIVE : INACTIVE;
        int border = pressed ? PURPLE : 0xFF454052;

        context.fill(x, y, x + width, y + height, border);
        context.fill(
            x + 1, y + 1,
            x + width - 1, y + height - 1,
            background
        );

        int textWidth = client.textRenderer.getWidth(label);
        int textX = x + (width - textWidth) / 2;
        int textY = y + (height - 8) / 2;

        context.drawText(
            client.textRenderer,
            label,
            textX,
            textY,
            WHITE,
            false
        );
    }
}
