package red.jackf.granulargamerules.client.mixins.screen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.worldselection.EditGameRulesScreen;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.level.GameRules;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import red.jackf.granulargamerules.client.impl.mixinutil.ChildRulesHolder;
import red.jackf.granulargamerules.client.impl.mixinutil.GGGameRuleEntry;
import red.jackf.granulargamerules.impl.GranularGamerules;

import java.util.Collection;
import java.util.List;

@Mixin(EditGameRulesScreen.GameRuleEntry.class)
public abstract class GameRuleEntryMixin extends EditGameRulesScreen.RuleEntry implements GGGameRuleEntry {
    public GameRuleEntryMixin(@Nullable List<FormattedCharSequence> tooltip) {
        super(tooltip);
    }

    @Shadow
    @NotNull
    public abstract List<GuiEventListener> children();

    @Unique
    private GameRules.Key<?> key = null;

    @Unique
    private GameRules.Key<?> parentKey = null;

    @Nullable
    @Unique
    private ChildRulesHolder childRulesHolder = null;

    @Override
    public boolean gg$shouldRenderBackground() {
        return (childRulesHolder != null && childRulesHolder.childrenShown) || this.parentKey != null;
    }

    @Override
    public void gg$setGameruleKey(GameRules.Key<?> key) {
        this.key = key;
    }

    @Override
    public GameRules.Key<?> gg$getGameruleKey() {
        return this.key;
    }

    @Override
    public void gg$setParentGameruleKey(GameRules.Key<?> key) {
        this.parentKey = key;
    }

    @Nullable
    @Override
    public GameRules.Key<?> gg$getParentGameruleKey() {
        return this.parentKey;
    }

    @Override
    public boolean gg$hasAdditions() {
        return childRulesHolder != null;
    }

    @Override
    public void gg$setChildEntries(Collection<EditGameRulesScreen.GameRuleEntry> childRules, EditGameRulesScreen.RuleList ruleList) {
        if (childRules.isEmpty()) return;
        this.childRulesHolder = new ChildRulesHolder(childRules, (EditGameRulesScreen.GameRuleEntry) (Object) this, ruleList);
        this.children().add(this.childRulesHolder.expandButton);
    }



    @Override
    public void gg$renderAdditions(GuiGraphics graphics, int mouseX, int mouseY, float partialTick, int index, int left, int top, int width, int height) {
        if (this.childRulesHolder != null) {
            this.childRulesHolder.render(graphics, mouseX, mouseY, partialTick, left, top);
        } else if (GranularGamerules.getParentRule(this.gg$getGameruleKey()).isPresent()) {

        }
    }
}
