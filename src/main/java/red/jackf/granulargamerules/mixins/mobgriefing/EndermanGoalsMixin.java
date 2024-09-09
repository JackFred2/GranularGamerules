package red.jackf.granulargamerules.mixins.mobgriefing;

import net.minecraft.world.level.GameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import red.jackf.granulargamerules.api.GGAPI;
import red.jackf.granulargamerules.impl.rules.MobGriefingRules;

@Mixin(targets = { "net/minecraft/world/entity/monster/EnderMan$EndermanLeaveBlockGoal", "net/minecraft/world/entity/monster/EnderMan$EndermanTakeBlockGoal"})
public class EndermanGoalsMixin {
    @Redirect(method = "canUse", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/GameRules;getBoolean(Lnet/minecraft/world/level/GameRules$Key;)Z"))
    private boolean checkGGRule(GameRules instance, GameRules.Key<GameRules.BooleanValue> key) {
        return GGAPI.getBoolean(instance, MobGriefingRules.ENDERMEN_MOVE_BLOCKS);
    }
}
