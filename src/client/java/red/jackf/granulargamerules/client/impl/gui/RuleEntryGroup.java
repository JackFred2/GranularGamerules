package red.jackf.granulargamerules.client.impl.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.worldselection.EditGameRulesScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.level.GameRules;
import red.jackf.granulargamerules.client.mixins.gamerulescreen.AbstractSelectionListAccessor;
import red.jackf.granulargamerules.impl.GranularGamerules;

import java.util.List;
import java.util.function.Supplier;

public final class RuleEntryGroup extends EditGameRulesScreen.RuleEntry {
    private static final ResourceLocation GROUP_BACKGROUND = GranularGamerules.id("gamerule_group/background");
    private static final WidgetSprites SPRITES = new WidgetSprites(
            GranularGamerules.id("gamerule_group/expanded"),
            GranularGamerules.id("gamerule_group/collapsed"),
            GranularGamerules.id("gamerule_group/expanded_highlighted"),
            GranularGamerules.id("gamerule_group/collapsed_highlighted")
    );

    private final EditGameRulesScreen.RuleList list;
    private final List<EditGameRulesScreen.RuleEntry> children;

    private final TwoStateButton button;

    private boolean isOpen = false;

    public RuleEntryGroup(EditGameRulesScreen.RuleList list, GameRules.Key<?> parentKey, List<EditGameRulesScreen.RuleEntry> children) {
        super(makeTooltip(parentKey, children));
        this.list = list;
        this.children = children;
        this.button = new TwoStateButton(10, 5, 16, 16, SPRITES, Component.empty(), this::cycleState, Supplier::get);
    }

    private static List<FormattedCharSequence> makeTooltip(GameRules.Key<?> parentKey, List<EditGameRulesScreen.RuleEntry> children) {
        return List.of(
                Component.literal(parentKey.getId()).withStyle(ChatFormatting.YELLOW).getVisualOrderText(),
                Component.translatable("gui.granularGamrules.expand.tooltip", children.size()).withStyle(ChatFormatting.GRAY).getVisualOrderText()
        );
    }

    private void cycleState(TwoStateButton button) {
        this.isOpen = !this.isOpen;
        button.setState(isOpen);

        int myIndex = this.list.children().indexOf(this);

        if (this.isOpen) {
            this.list.children().addAll(myIndex + 1, this.children);
        } else {
            this.list.children().removeAll(this.children);
        }
    }

    @Override
    public void render(GuiGraphics graphics, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean hovering, float partialTick) {
        // show background on open group
        if (this.isOpen) {
            this.renderGroupBackground(graphics, top, left, width);
        }

        this.button.setX(left);
        this.button.setY(top);
        this.button.render(graphics, mouseX, mouseY, partialTick);

        graphics.drawString(
                Minecraft.getInstance().font,
                Component.translatable("gui.granularGamrules.expand"),
                left + 20,
                top + 5,
                0xFF_FFFFFF
        );
    }

    private void renderGroupBackground(GuiGraphics graphics, int top, int left, int width) {
        int totalHeight = ((AbstractSelectionListAccessor) this.list).getItemHeight() * (1 + this.children.size());
        int margin = 2;

        RenderSystem.enableBlend();
        graphics.blitSprite(GROUP_BACKGROUND,
                left - margin - 1,
                top - margin,
                width + 2 * margin,
                totalHeight + 2 * margin - 4);
        RenderSystem.disableBlend();
    }

    @Override
    public List<? extends NarratableEntry> narratables() {
        return List.of(this.button);
    }

    @Override
    public List<? extends GuiEventListener> children() {
        return List.of(this.button);
    }
}
