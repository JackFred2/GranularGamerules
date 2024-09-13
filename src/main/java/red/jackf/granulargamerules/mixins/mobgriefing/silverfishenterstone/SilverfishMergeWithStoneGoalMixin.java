package red.jackf.granulargamerules.mixins.mobgriefing.silverfishenterstone;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.level.GameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import red.jackf.granulargamerules.api.GGAPI;
import red.jackf.granulargamerules.impl.rules.MobGriefingRules;

@Mixin(targets = "net/minecraft/world/entity/monster/Silverfish$SilverfishMergeWithStoneGoal")
public class SilverfishMergeWithStoneGoalMixin {
    @WrapOperation(method = "canUse", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/GameRules;getBoolean(Lnet/minecraft/world/level/GameRules$Key;)Z"))
    private boolean checkGGRule(GameRules instance, GameRules.Key<GameRules.BooleanValue> key, Operation<Boolean> original) {
        return GGAPI.getBoolean(instance, MobGriefingRules.SILVERFISH_ENTER_STONE);
    }
}
