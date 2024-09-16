package red.jackf.granulargamerules.mixins.universalanger;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.ai.goal.target.ResetUniversalAngerTargetGoal;
import net.minecraft.world.level.GameRules;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import red.jackf.granulargamerules.impl.rules.UniversalAngerRules;

@Mixin(ResetUniversalAngerTargetGoal.class)
public abstract class ResetUniversalAngerTargetGoalMixin<T extends Mob & NeutralMob>  {
    @Shadow @Final private T mob;

    @WrapOperation(method = "canUse", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/GameRules;getBoolean(Lnet/minecraft/world/level/GameRules$Key;)Z"))
    private boolean checkGGRule(GameRules instance, GameRules.Key<GameRules.BooleanValue> parent, Operation<Boolean> original) {
        return UniversalAngerRules.testOverride(instance, this.mob).orElse(original.call(instance, parent));
    }
}
