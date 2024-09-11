package red.jackf.granulargamerules.client.impl.mixinutil;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.worldselection.EditGameRulesScreen;
import net.minecraft.world.level.GameRules;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public interface GGGameRuleEntry {
    void gg$setGameruleKey(GameRules.Key<?> key);

    void gg$setParentGameruleKey(GameRules.Key<?> key);

    GameRules.Key<?> gg$getGameruleKey();

    @Nullable
    GameRules.Key<?> gg$getParentGameruleKey();

    boolean gg$hasAdditions();

    boolean gg$shouldRenderBackground();

    void gg$setChildEntries(Collection<EditGameRulesScreen.GameRuleEntry> children, EditGameRulesScreen.RuleList ruleList);

    void gg$renderAdditions(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick, int index, int left, int top, int width, int height);
}
