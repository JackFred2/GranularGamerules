package red.jackf.granulargamerules.client.mixins.gamerulescreen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.screens.worldselection.EditGameRulesScreen;
import net.minecraft.world.level.GameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import red.jackf.granulargamerules.client.impl.gui.RuleEntryGroup;
import red.jackf.granulargamerules.client.impl.mixinutil.GGEntryKeyHolder;
import red.jackf.granulargamerules.impl.GranularGamerules;

import java.util.*;

/**
 * Post processes the list of entries for the screen and groups them if needed.
 *
 * @author JackFred
 */
@Mixin(EditGameRulesScreen.RuleList.class)
public abstract class GameRulesScreenRuleListMixin extends ContainerObjectSelectionList<EditGameRulesScreen.RuleEntry> {
    public GameRulesScreenRuleListMixin(Minecraft minecraft, int width, int height, int y, int itemHeight) {
        super(minecraft, width, height, y, itemHeight);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void lookOverEntriesAndGroupThem(EditGameRulesScreen editGameRulesScreen, GameRules gameRules, CallbackInfo ci) {
        for (Map.Entry<GameRules.Key<?>, Collection<GameRules.Key<?>>> entry : GranularGamerules.getChildGamerules().asMap().entrySet()) {
            GameRules.Key<?> parentKey = entry.getKey();
            int index = getIndexOf(parentKey);
            if (index == -1) {
                GranularGamerules.LOGGER.warn("Couldn't get entry for gamerule {}", parentKey.getId());
                return;
            }

            List<EditGameRulesScreen.RuleEntry> listEntries = entry.getValue().stream()
                    .map(this::getAndRemoveEntryFor)
                    .filter(Objects::nonNull)
                    .toList();

            this.children().add(index + 1, new RuleEntryGroup((EditGameRulesScreen.RuleList) (Object) this, parentKey, listEntries));
        }
    }

    @Unique
    private int getIndexOf(GameRules.Key<?> key) {
        for (int i = 0; i < this.children().size(); i++) {
            EditGameRulesScreen.RuleEntry entry = this.children().get(i);
            if (((GGEntryKeyHolder) entry).gg$getKey() == key) return i;
        }

        return -1;
    }

    // O(n^2) but non issue at this point
    @Unique
    private EditGameRulesScreen.RuleEntry getAndRemoveEntryFor(GameRules.Key<?> key) {
        for (Iterator<EditGameRulesScreen.RuleEntry> iterator = this.children().iterator(); iterator.hasNext(); ) {
            EditGameRulesScreen.RuleEntry child = iterator.next();
            if (((GGEntryKeyHolder) child).gg$getKey() == key) {
                iterator.remove();
                return child;
            }
        }

        return null;
    }
}
