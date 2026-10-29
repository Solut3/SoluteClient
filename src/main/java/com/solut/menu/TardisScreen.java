package com.solut.menu;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

import java.util.List;

public final class TardisScreen extends Screen {
    private List<AitIntegration.TardisInfo> tardises = List.of();
    private int selected = 0;
    private String status = "Aguardando deteccao do AIT";

    public TardisScreen() {
        super(Text.literal("Solute Menu - TARDIS"));
    }

    private void refreshTardises() {
        tardises = AitIntegration.discover();

        if (tardises.isEmpty()) {
            selected = 0;
            status = "Nenhuma TARDIS detectada no cliente.";
        } else {
            selected = Math.min(selected, tardises.size() - 1);
            status = tardises.size() + " TARDIS(es) detectada(s).";
        }
    }

    private String selectedId() {
        if (tardises.isEmpty()) return null;
        return tardises.get(selected).id().toString();
    }

    private void command(String command, String message) {
        AitIntegration.sendCommand(command);
        status = message + " Confira a resposta do servidor.";
    }

    @Override
    protected void init() {
        refreshTardises();

        int c = width / 2;
        int y = height - 96;

        addDrawableChild(ButtonWidget.builder(
            Text.literal("Atualizar"),
            b -> refreshTardises()
        ).dimensions(c - 155, y, 100, 20).build());

        addDrawableChild(ButtonWidget.builder(
            Text.literal("Anterior"),
            b -> {
                if (!tardises.isEmpty()) {
                    selected = Math.floorMod(selected - 1, tardises.size());
                }
            }
        ).dimensions(c - 50, y, 95, 20).build());

        addDrawableChild(ButtonWidget.builder(
            Text.literal("Proxima"),
            b -> {
                if (!tardises.isEmpty()) {
                    selected = (selected + 1) % tardises.size();
                }
            }
        ).dimensions(c + 50, y, 95, 20).build());

        addDrawableChild(ButtonWidget.builder(
            Text.literal("Desbloquear"),
            b -> {
                String id = selectedId();
                if (id != null) command("ait lock " + id + " false",
                    "Solicitacao de desbloqueio enviada.");
            }
        ).dimensions(c - 155, y + 24, 150, 20).build());

        addDrawableChild(ButtonWidget.builder(
            Text.literal("Bloquear"),
            b -> {
                String id = selectedId();
                if (id != null) command("ait lock " + id + " true",
                    "Solicitacao de bloqueio enviada.");
            }
        ).dimensions(c + 5, y + 24, 150, 20).build());

        addDrawableChild(ButtonWidget.builder(
            Text.literal("Combustivel maximo"),
            b -> {
                String id = selectedId();
                if (id != null) command("ait fuel set " + id + " 50000",
                    "Solicitacao de combustivel enviada.");
            }
        ).dimensions(c - 155, y + 48, 150, 20).build());

        addDrawableChild(ButtonWidget.builder(
            Text.literal("Invocar TARDIS"),
            b -> {
                String id = selectedId();
                if (id != null) command("ait summon " + id,
                    "Solicitacao de invocacao enviada.");
            }
        ).dimensions(c + 5, y + 48, 150, 20).build());

        addDrawableChild(ButtonWidget.builder(
            Text.literal("Voltar"),
            b -> {
                if (client != null) client.setScreen(new SoluteMenuScreen(0));
            }
        ).dimensions(c - 60, height - 27, 120, 20).build());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context);

        int c = width / 2;
        context.fill(c - 145, 25, c + 145, height - 15, 0xF0181A22);
        context.fill(c - 145, 25, c + 145, 28, 0xFFB56CFF);

        context.drawCenteredTextWithShadow(
            textRenderer, "SOLUTE MENU - TARDIS", c, 40, 0xFFFFFFFF
        );
        context.drawCenteredTextWithShadow(
            textRenderer, "Adventures in Time", c, 57, 0xFFB56CFF
        );

        context.drawTextWithShadow(
            textRenderer, status, c - 130, 78, 0xFFFFFFFF
        );

        if (tardises.isEmpty()) {
            context.drawTextWithShadow(
                textRenderer, "Nenhuma TARDIS encontrada.",
                c - 130, 105, 0xFFB8B9C9
            );
        } else {
            AitIntegration.TardisInfo t = tardises.get(selected);

            context.drawCenteredTextWithShadow(
                textRenderer, "TARDIS selecionada: " + (selected + 1)
                    + "/" + tardises.size(), c, 101, 0xFFFFFFFF
            );

            context.drawCenteredTextWithShadow(
                textRenderer, t.description(), c, 119, 0xFFD9C5FF
            );

            context.drawCenteredTextWithShadow(
                textRenderer, t.id().toString(), c, 137, 0xFFB8B9C9
            );
        }

        context.drawCenteredTextWithShadow(
            textRenderer,
            "Os comandos dependem das permissoes do servidor.",
            c, height - 47, 0xFFB8B9C9
        );

        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}