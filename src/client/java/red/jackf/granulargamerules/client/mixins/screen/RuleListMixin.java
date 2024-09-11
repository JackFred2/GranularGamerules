package red.jackf.granulargamerules.client.mixins.screen;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.screens.worldselection.EditGameRulesScreen;
import net.minecraft.world.level.GameRules;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import red.jackf.granulargamerules.client.impl.mixinutil.GGGameRuleEntry;
import red.jackf.granulargamerules.client.impl.util.ListUtil;
import red.jackf.granulargamerules.impl.GranularGamerules;

import java.util.*;

@Mixin(EditGameRulesScreen.RuleList.class)
public abstract class RuleListMixin extends ContainerObjectSelectionList<EditGameRulesScreen.RuleEntry> {

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
                    ggEntry.gg$setParentGameruleKey(parent.get());
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
}
