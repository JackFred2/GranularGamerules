package red.jackf.granulargamerules.client.mixins.screen;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSelectionList;
import net.minecraft.client.gui.screens.worldselection.EditGameRulesScreen;
import net.minecraft.world.level.GameRules;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import red.jackf.granulargamerules.client.impl.mixinutil.ChildRulesHolder;
import red.jackf.granulargamerules.client.impl.mixinutil.GGGameRuleEntry;
import red.jackf.granulargamerules.client.impl.screen.GroupRenderer;
import red.jackf.granulargamerules.client.impl.util.ListUtil;
import red.jackf.granulargamerules.impl.GranularGamerules;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Debug(export = true)
@Mixin(AbstractSelectionList.class)
public abstract class AbstractSelectionListMixin<E extends AbstractSelectionList.Entry<E>> {

    @Unique
    private final Set<GameRules.Key<?>> alreadyRendered = new HashSet<>();

    @Shadow protected abstract int getItemCount();

    @Shadow protected abstract E getEntry(int index);

    @Shadow @Final protected int itemHeight;

    @Shadow public abstract List<E> children();

    @Inject(method = "renderItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/AbstractSelectionList$Entry;render(Lnet/minecraft/client/gui/GuiGraphics;IIIIIIIZF)V"))
    private void renderAdditions(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick, int index, int left, int top, int width, int height, CallbackInfo ci, @Local E entry) {
        //noinspection ConstantValue
        if ((Object) this instanceof EditGameRulesScreen.RuleList && entry instanceof GGGameRuleEntry ruleEntry) {
            GameRules.Key<?> key = ruleEntry.gg$getParentGameruleKey() != null ? ruleEntry.gg$getParentGameruleKey() : ruleEntry.gg$getGameruleKey();
            if (ruleEntry.gg$shouldRenderBackground() && !alreadyRendered.contains(key)) {
                alreadyRendered.add(key);

                int offset = index - ListUtil.getIndexOf(this.children(), entry2 -> entry2 instanceof GGGameRuleEntry ruleEntry2 && ruleEntry2.gg$getGameruleKey() == key);

                final int height2 = this.itemHeight * (GranularGamerules.getChildRules(key).size() + 1);

                GroupRenderer.render(guiGraphics, left, top - this.itemHeight * offset, width, height2);
            }

            ruleEntry.gg$renderAdditions(guiGraphics, mouseX, mouseY, partialTick, index, left, top, width, height);
        }
    }

    @Inject(method = "renderListItems", at = @At("TAIL"))
    private void clearAlreadyRendered(GuiGraphics graphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        alreadyRendered.clear();
    }

    @Definition(id = "mouseX", local = @Local(type = double.class, ordinal = 0, argsOnly = true))
    @Expression("mouseX >= ?")
    @WrapOperation(method = "getEntryAtPosition", at = @At("MIXINEXTRAS:EXPRESSION"))
    private boolean offsetForGGDropdownButton(double mouseX, double left, Operation<Boolean> operation, @Local(ordinal = 5) int entryIndex) {
        if ((Object) this instanceof EditGameRulesScreen.RuleList)
            if (entryIndex >= 0 && entryIndex < this.getItemCount())
                if (this.getEntry(entryIndex) instanceof GGGameRuleEntry gameRuleEntry && gameRuleEntry.gg$hasAdditions())
                    left = left - 24;
        return operation.call(mouseX, left);
    }
}
