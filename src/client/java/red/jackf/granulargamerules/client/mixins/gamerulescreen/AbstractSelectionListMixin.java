package red.jackf.granulargamerules.client.mixins.gamerulescreen;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSelectionList;
import net.minecraft.client.gui.screens.worldselection.EditGameRulesScreen;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import red.jackf.granulargamerules.client.impl.mixinutil.GGChildEntryHolder;

/**
 * Used to enable rendering of the group expand / collapse buttons, as well as interaction for them
 *
 * @author JackFred
 */
@Mixin(AbstractSelectionList.class)
@Debug(export = true)
public abstract class AbstractSelectionListMixin<E extends AbstractSelectionList.Entry<E>> {
    @Shadow
    protected abstract E getEntry(int index);

    @Shadow protected abstract int getItemCount();

    @Inject(method = "renderItem", at = @At(value = "TAIL"))
    private void renderExtras(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick, int index, int left, int top, int width, int height, CallbackInfo ci) {
        if (this.getEntry(index) instanceof GGChildEntryHolder entryHolder) {
            entryHolder.gg$renderExtraButton(guiGraphics, mouseX, mouseY, partialTick, index, left, top, width, height);
        }
    }

    @Inject(method = "getEntryAtPosition",
            at = @At(value = "FIELD",
                    target = "Lnet/minecraft/client/gui/components/AbstractSelectionList;itemHeight:I",
                    opcode = Opcodes.GETFIELD,
                    shift = At.Shift.BY,
                    by = 3))
    private void offsetForGGDropdownButton(double mouseX, double mouseY, CallbackInfoReturnable<E> cir, @Local(ordinal = 2) LocalIntRef left, @Local(ordinal = 5) int entryIndex) {
        if ((Object) this instanceof EditGameRulesScreen.RuleList)
            if (entryIndex >= 0 && entryIndex < this.getItemCount())
                if (this.getEntry(entryIndex) instanceof GGChildEntryHolder childEntryHolder && childEntryHolder.gg$hasGroupButton())
                    left.set(left.get() - 20);
    }
}
