package red.jackf.granulargamerules.mixins.mobgriefing.mobstramplefarmland;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.block.FarmBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import red.jackf.granulargamerules.impl.GGDeferredChecker;
import red.jackf.granulargamerules.impl.rules.MobGriefingRules;

@Mixin(FarmBlock.class)
public class FarmBlockMixin {
    @WrapOperation(method = "fallOn", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/GameRules;getBoolean(Lnet/minecraft/world/level/GameRules$Key;)Z"))
    private boolean checkGGRule(GameRules instance, GameRules.Key<GameRules.BooleanValue> parent, Operation<Boolean> original) {
        return GGDeferredChecker.getBoolean(instance, MobGriefingRules.MOBS_TRAMPLE_FARMLAND).orElse(original.call(instance, parent));
    }
}
