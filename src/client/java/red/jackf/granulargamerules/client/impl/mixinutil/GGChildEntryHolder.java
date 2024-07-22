package red.jackf.granulargamerules.client.impl.mixinutil;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.worldselection.EditGameRulesScreen;
import net.minecraft.util.FormattedCharSequence;

import java.util.List;

public interface GGChildEntryHolder {
    void gg$setChildEntries(EditGameRulesScreen.RuleList list, List<EditGameRulesScreen.RuleEntry> children);

    void gg$renderExtraButton(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick, int index, int left, int top, int width, int height);

    boolean gg$hasGroupButton();

    boolean gg$isButtonHovered();

    List<FormattedCharSequence> gg$getButtonTooltip();
}
