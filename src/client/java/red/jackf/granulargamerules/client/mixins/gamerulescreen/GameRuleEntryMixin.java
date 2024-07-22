package red.jackf.granulargamerules.client.mixins.gamerulescreen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.worldselection.EditGameRulesScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import red.jackf.granulargamerules.client.impl.gui.TwoStateButton;
import red.jackf.granulargamerules.client.impl.mixinutil.GGChildEntryHolder;
import red.jackf.granulargamerules.impl.GranularGamerules;

import java.util.List;
import java.util.function.Supplier;

/**
 * Adds and handles expanding and collapsing of the game rule groups in granular gamerules, as well as rendering and
 * creating the button.
 *
 * @author JackFred
 */
@Mixin(EditGameRulesScreen.GameRuleEntry.class)
public abstract class GameRuleEntryMixin extends EditGameRulesScreen.RuleEntry implements GGChildEntryHolder {
    @Unique
    private static final ResourceLocation GROUP_BACKGROUND = GranularGamerules.id("gamerule_group/background");
    @Unique
    private static final WidgetSprites BUTTON_SPRITES = new WidgetSprites(
            GranularGamerules.id("gamerule_group/expanded"),
            GranularGamerules.id("gamerule_group/collapsed"),
            GranularGamerules.id("gamerule_group/expanded_highlighted"),
            GranularGamerules.id("gamerule_group/collapsed_highlighted")
    );
    @Shadow
    @NotNull
    public abstract List<GuiEventListener> children();

    @Unique
    @Nullable
    private TwoStateButton expandChildrenButton = null;
    @Unique
    @Nullable
    private EditGameRulesScreen.RuleList list = null;

    @Unique
    private List<EditGameRulesScreen.RuleEntry> childEntries;

    @Unique
    private boolean isOpen = false;

    public GameRuleEntryMixin(@Nullable List<FormattedCharSequence> tooltip) {
        super(tooltip);
    }

    public void gg$setChildEntries(EditGameRulesScreen.RuleList list, List<EditGameRulesScreen.RuleEntry> children) {
        this.list = list;
        this.expandChildrenButton = new TwoStateButton(10, 5, 16, 16, BUTTON_SPRITES, Component.empty(), this::cycleState, Supplier::get);
        this.children().add(expandChildrenButton);
        this.childEntries = children;
    }

    @Override
    public void gg$renderExtraButton(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick, int index, int left, int top, int width, int height) {
        if (this.expandChildrenButton == null || this.list == null) return;

        if (this.isOpen) {
            int fullHeight = ((AbstractSelectionListAccessor) this.list).getItemHeight() * (1 + this.childEntries.size());

            RenderSystem.enableBlend();
            guiGraphics.blitSprite(GROUP_BACKGROUND, left - 20 - 4, top - 2, width + 20 + 5, fullHeight);
            RenderSystem.disableBlend();
        }

        this.expandChildrenButton.setX(left - 20);
        this.expandChildrenButton.setY(top);
        this.expandChildrenButton.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean gg$hasGroupButton() {
        return expandChildrenButton != null;
    }

    @Override
    public boolean gg$isButtonHovered() {
        return this.expandChildrenButton != null && this.expandChildrenButton.isHovered();
    }

    @Override
    public List<FormattedCharSequence> gg$getButtonTooltip() {
        if (this.expandChildrenButton == null) return List.of();
        return List.of(
                Component.translatable("gui.granularGamrules.expand").withStyle(ChatFormatting.YELLOW).getVisualOrderText(),
                GranularGamerules.MOD_TITLE.copy().withStyle(ChatFormatting.GRAY).getVisualOrderText()
        );
    }

    @Unique
    private void cycleState(TwoStateButton button) {
        if (this.expandChildrenButton == null || this.list == null) return;
        this.isOpen = !this.isOpen;
        button.setState(isOpen);

        int myIndex = this.list.children().indexOf(this);

        if (this.isOpen) {
            this.list.children().addAll(myIndex + 1, this.childEntries);
        } else {
            this.list.children().removeAll(this.childEntries);
        }
    }
}
