package red.jackf.granulargamerules.client.mixins.gamerulescreen;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.screens.worldselection.EditGameRulesScreen;
import net.minecraft.world.level.GameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import red.jackf.granulargamerules.client.impl.mixinutil.GGChildEntryHolder;
import red.jackf.granulargamerules.client.impl.mixinutil.GGEntryKeyHolder;
import red.jackf.granulargamerules.impl.GranularGamerules;

import java.util.*;

/**
 * Post processes the list of entries for the screen and groups them if needed.
 *
 * @author JackFred
 */
@Mixin(EditGameRulesScreen.RuleList.class)
public abstract class RuleListMixin extends ContainerObjectSelectionList<EditGameRulesScreen.RuleEntry> {
    @Shadow(aliases = "field_24313", remap = false)
    private EditGameRulesScreen parentScreen;

    public RuleListMixin(Minecraft minecraft, int width, int height, int y, int itemHeight) {
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
            EditGameRulesScreen.RuleEntry parentEntry = this.children().get(index);
            if (parentEntry instanceof EditGameRulesScreen.GameRuleEntry gameRuleEntry) {
                List<EditGameRulesScreen.RuleEntry> listEntries = entry.getValue().stream()
                        .map(this::getAndRemoveEntryFor)
                        .filter(Objects::nonNull)
                        .toList();

                ((GGChildEntryHolder) gameRuleEntry).gg$setChildEntries((EditGameRulesScreen.RuleList) (Object) this, listEntries);
            }
        }
    }

    @Inject(method = "renderWidget", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/worldselection/EditGameRulesScreen$RuleList;getHovered()Lnet/minecraft/client/gui/components/AbstractSelectionList$Entry;", shift = At.Shift.BY, by = 3), cancellable = true)
    private void changeTooltipForButton(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci, @Local EditGameRulesScreen.RuleEntry entry) {
        if (entry instanceof GGChildEntryHolder childEntryHolder && childEntryHolder.gg$hasGroupButton()) {
            if (((GGChildEntryHolder) entry).gg$isButtonHovered()) {
                parentScreen.setTooltipForNextRenderPass(childEntryHolder.gg$getButtonTooltip());
                ci.cancel();
            }
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
