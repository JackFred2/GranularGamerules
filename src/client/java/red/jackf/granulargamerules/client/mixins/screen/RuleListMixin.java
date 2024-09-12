package red.jackf.granulargamerules.client.mixins.screen;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.screens.worldselection.EditGameRulesScreen;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.level.GameRules;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import red.jackf.granulargamerules.client.impl.mixinutil.GGGameRuleEntry;
import red.jackf.granulargamerules.client.impl.util.ListUtil;
import red.jackf.granulargamerules.impl.GranularGamerules;

import java.util.*;

@Mixin(EditGameRulesScreen.RuleList.class)
public abstract class RuleListMixin extends ContainerObjectSelectionList<EditGameRulesScreen.RuleEntry> {

    @Shadow(aliases = "field_24313", remap = false)
    @Final
    private EditGameRulesScreen parentScreen;

    private RuleListMixin(Minecraft minecraft, int width, int height, int y, int itemHeight) {
        super(minecraft, width, height, y, itemHeight);
        throw new AssertionError();
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void postProcessEntries(EditGameRulesScreen screen, GameRules rules, CallbackInfo ci) {
        Multimap<GameRules.Key<?>, EditGameRulesScreen.GameRuleEntry> keyToChildEntries = MultimapBuilder.hashKeys().arrayListValues().build();

        // remove child rules
        for (Iterator<EditGameRulesScreen.RuleEntry> iterator = this.children().iterator(); iterator.hasNext(); ) {
            EditGameRulesScreen.RuleEntry entry = iterator.next();
            if (entry instanceof EditGameRulesScreen.GameRuleEntry gameRuleEntry && entry instanceof GGGameRuleEntry ggEntry) {
                Optional<? extends GameRules.Key<?>> parent = GranularGamerules.getParentRule(ggEntry.gg$getGameruleKey());
                if (parent.isPresent()) {
                    ggEntry.gg$setParentGameruleKey(parent.get(), rules);
                    keyToChildEntries.put(parent.get(), gameRuleEntry);
                    iterator.remove();
                }
            }
        }

        for (Map.Entry<GameRules.Key<?>, Collection<EditGameRulesScreen.GameRuleEntry>> entry : keyToChildEntries.asMap().entrySet()) {
            @Nullable
            EditGameRulesScreen.RuleEntry parent = ListUtil.get(this.children(), entry2 -> entry2 instanceof GGGameRuleEntry gameRuleEntry && gameRuleEntry.gg$getGameruleKey() == entry.getKey());

            if (parent != null) {
                ((GGGameRuleEntry) parent).gg$setChildEntries(entry.getValue(), (EditGameRulesScreen.RuleList) (Object) this);
            }
        }
    }

    @Definition(id = "ruleEntry", local = @Local(type = EditGameRulesScreen.RuleEntry.class))
    @Expression("ruleEntry != null")
    @Inject(method = "renderWidget", at = @At(value = "MIXINEXTRAS:EXPRESSION"), cancellable = true)
    private void changeTooltipForButton(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci, @Local EditGameRulesScreen.RuleEntry entry) {
        if (entry instanceof GGGameRuleEntry gameRuleEntry) {
            Optional<List<FormattedCharSequence>> tooltipOverride = gameRuleEntry.gg$getTooltipOverride();

            if (tooltipOverride.isPresent()) {
                this.parentScreen.setTooltipForNextRenderPass(tooltipOverride.get());
                ci.cancel();
            }
        }
    }
}
