package red.jackf.granulargamerules.mixins.mobgriefing;

import net.minecraft.world.entity.ai.goal.BreakDoorGoal;
import net.minecraft.world.level.GameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import red.jackf.granulargamerules.api.GGAPI;
import red.jackf.granulargamerules.impl.rules.MobGriefingRules;

@Mixin(BreakDoorGoal.class)
public class BreakDoorGoalMixin {
    @Redirect(method = "canUse", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/GameRules;getBoolean(Lnet/minecraft/world/level/GameRules$Key;)Z"))
    private boolean checkGGRule(GameRules instance, GameRules.Key<GameRules.BooleanValue> ignored) {
        return GGAPI.getBoolean(instance, MobGriefingRules.ZOMBIES_BREAK_DOORS);
    }
}
