
package com.solut.menu;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public class SoluteMenuScreen extends Screen {

    private static final String[] TABS = {
        "Visual",
        "Jogador",
        "Utilitarios",
        "Admin",
        "TARDIS"
    };

    private static final String[][] MODULES = {
        {
            "HUD: Jogador",
            "HUD: Coordenadas",
            "HUD: FPS",
            "HUD: Keystrokes"
        },
        {
            "Mensagens de status",
            "Avisos na tela",
            "Controles"
        },
        {
            "Modo compacto",
            "Notificacoes",
            "Painel de informacoes"
        },
        {
            "Invisibilidade",
            "Localizador",
            "X-ray"
        },
        {
            "TARDIS detectadas",
            "Localizar TARDIS",
            "Convocar TARDIS",
            "Tranca e combustivel",
            "Viagem dimensional"
        }
    };

    private static final boolean[][] STATES = new boolean[5][5];

    private static final int PANEL_WIDTH = 440;
    private static final int PANEL_HEIGHT = 280;

    private final int selectedTab;

    public SoluteMenuScreen() {
        this(0);
    }

    public SoluteMenuScreen(int selectedTab) {
        super(Text.literal("Solute Menu"));
        this.selectedTab = Math.max(0, Math.min(selectedTab, TABS.length - 1));
    }

    private int left() {
        return (width - PANEL_WIDTH) / 2;
    }

    private int top() {
        return (height - PANEL_HEIGHT) / 2;
    }

    private boolean canUseAdmin() {
        MinecraftClient client = MinecraftClient.getInstance();

        return client.player != null
            && client.world != null
            && client.player.hasPermissionLevel(2);
    }

    private boolean getState(int index) {
        if (selectedTab == 0) {
            return switch (index) {
                case 0 -> HudSettings.playerInfo;
                case 1 -> HudSettings.coordinates;
                case 2 -> HudSettings.fps;
                case 3 -> HudSettings.keystrokes;
                default -> false;
            };
        }

        if (selectedTab == 3) {
            return switch (index) {
                case 0 -> AdminFeatures.isInvisibilityRequested();
                case 1 -> AdminFeatures.isLocatorEnabled();
                case 2 -> AdminFeatures.isXrayEnabled();
                default -> false;
            };
        }

        return STATES[selectedTab][index];
    }

    private void toggleState(int index) {
        if (selectedTab == 0) {
            switch (index) {
                case 0 -> HudSettings.togglePlayerInfo();
                case 1 -> HudSettings.toggleCoordinates();
                case 2 -> HudSettings.toggleFps();
                case 3 -> HudSettings.toggleKeystrokes();
            }
            return;
        }

        if (selectedTab == 3) {
            if (!canUseAdmin()) {
                return;
            }

            switch (index) {
                case 0 -> AdminFeatures.toggleInvisibility();
                case 1 -> AdminFeatures.toggleLocator();
                case 2 -> AdminFeatures.toggleXray();
            }
            return;
        }

        STATES[selectedTab][index] = !STATES[selectedTab][index];
    }

    @Override
    protected void init() {
        int x = left();
        int y = top();

        for (int i = 0; i < TABS.length; i++) {
            final int tab = i;

            addDrawableChild(
                ButtonWidget.builder(
                    Text.literal(
                        (selectedTab == i ? "> " : "") + TABS[i]
                    ),
                    button -> {
                        if (client == null) {
                            return;
                        }

                        if (tab == 4) {
                            client.setScreen(new TardisScreen());
                            return;
                        }

                        if (tab == 3 && !canUseAdmin()) {
                            if (client.player != null) {
                                client.player.sendMessage(
                                    Text.literal(
                                        "§c[Solute] Acesso administrativo indisponivel."
                                    ),
                                    true
                                );
                            }
                            return;
                        }

                        client.setScreen(new SoluteMenuScreen(tab));
                    }
                ).dimensions(x + 14, y + 52 + i * 34, 118, 25).build()
            );
        }

        if (selectedTab == 4) {
            addDrawableChild(
                ButtonWidget.builder(
                    Text.literal("Abrir painel TARDIS"),
                    button -> {
                        if (client != null) {
                            client.setScreen(new TardisScreen());
                        }
                    }
                ).dimensions(x + 150, y + 75, 270, 30).build()
            );
        } else if (selectedTab == 3 && !canUseAdmin()) {
            addDrawableChild(
                ButtonWidget.builder(
                    Text.literal("Sem permissao de OP"),
                    button -> {}
                ).dimensions(x + 150, y + 75, 270, 30).build()
            );
        } else {
            for (int i = 0; i < MODULES[selectedTab].length; i++) {
                final int index = i;

                addDrawableChild(
                    ButtonWidget.builder(
                        Text.literal(
                            MODULES[selectedTab][i]
                                + "  ["
                                + (getState(i) ? "ON" : "OFF")
                                + "]"
                        ),
                        button -> {
                            toggleState(index);

                            if (client != null) {
                                client.setScreen(
                                    new SoluteMenuScreen(selectedTab)
                                );
                            }
                        }
                    ).dimensions(x + 150, y + 58 + i * 43, 270, 30).build()
                );
            }
        }

        addDrawableChild(
            ButtonWidget.builder(
                Text.literal("Fechar"),
                button -> {
                    if (client != null) {
                        client.setScreen(null);
                    }
                }
            ).dimensions(x + 320, y + 235, 100, 25).build()
        );
    }

    @Override
    public void render(
        DrawContext context,
        int mouseX,
        int mouseY,
        float delta
    ) {
        renderBackground(context);

        int x = left();
        int y = top();

        context.fill(
            x, y,
            x + PANEL_WIDTH, y + PANEL_HEIGHT,
            0xF0181A22
        );

        context.fill(
            x, y,
            x + PANEL_WIDTH, y + 3,
            0xFFB56CFF
        );

        context.fill(
            x + 140, y + 42,
            x + 141, y + 220,
            0xFF383B49
        );

        context.drawText(
            textRenderer,
            "SOLUTE MENU",
            x + 15, y + 15,
            0xFFFFFFFF,
            false
        );

        context.drawText(
            textRenderer,
            TABS[selectedTab],
            x + 150, y + 15,
            0xFFB56CFF,
            false
        );

        context.drawText(
            textRenderer,
            "Minecraft 1.20.1 | Fabric",
            x + 150, y + 36,
            0xFFB8B9C9,
            false
        );

        context.drawText(
            textRenderer,
            "Right Shift: abrir/fechar",
            x + 15, y + 245,
            0xFFB8B9C9,
            false
        );

        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
