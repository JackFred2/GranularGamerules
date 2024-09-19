package red.jackf.granulargamerules.mixins.mobgriefing.mobscrushturtleeggs;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.entity.ai.goal.RemoveBlockGoal;
import net.minecraft.world.level.GameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import red.jackf.granulargamerules.impl.GGDeferredChecker;
import red.jackf.granulargamerules.impl.rules.MobGriefingRules;

@Mixin(RemoveBlockGoal.class)
public class RemoveBlockGoalMixin {

    @Definition(id = "removerMob", field = "Lnet/minecraft/world/entity/ai/goal/RemoveBlockGoal;removerMob:Lnet/minecraft/world/entity/Mob;")
    @Definition(id = "level", method = "Lnet/minecraft/world/entity/Mob;level()Lnet/minecraft/world/level/Level;")
    @Definition(id = "getGameRules", method = "Lnet/minecraft/world/level/Level;getGameRules()Lnet/minecraft/world/level/GameRules;")
    @Definition(id = "getBoolean", method = "Lnet/minecraft/world/level/GameRules;getBoolean(Lnet/minecraft/world/level/GameRules$Key;)Z")
    @Definition(id = "RULE_MOBGRIEFING", field = "Lnet/minecraft/world/level/GameRules;RULE_MOBGRIEFING:Lnet/minecraft/world/level/GameRules$Key;")
    @Expression("this.removerMob.level().getGameRules().getBoolean(RULE_MOBGRIEFING)")
    @WrapOperation(method = "canUse", at = @At("MIXINEXTRAS:EXPRESSION"))
    private boolean checkGGRule(GameRules instance, GameRules.Key<GameRules.BooleanValue> key, Operation<Boolean> original) {
        return GGDeferredChecker.getBoolean(instance, MobGriefingRules.MOBS_CRUSH_TURTLE_EGGS).orElse(original.call(instance, key));
    }
}
