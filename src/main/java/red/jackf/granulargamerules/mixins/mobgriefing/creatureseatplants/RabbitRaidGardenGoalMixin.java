package red.jackf.granulargamerules.mixins.mobgriefing.creatureseatplants;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.level.GameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import red.jackf.granulargamerules.impl.GGDeferredChecker;
import red.jackf.granulargamerules.impl.rules.MobGriefingRules;

@Mixin(targets = "net/minecraft/world/entity/animal/Rabbit$RaidGardenGoal")
public class RabbitRaidGardenGoalMixin {
    @Definition(id = "rabbit", field = "Lnet/minecraft/world/entity/animal/Rabbit$RaidGardenGoal;rabbit:Lnet/minecraft/world/entity/animal/Rabbit;")
    @Definition(id = "level", method = "Lnet/minecraft/world/entity/animal/Rabbit;level()Lnet/minecraft/world/level/Level;")
    @Definition(id = "getGameRules", method = "Lnet/minecraft/world/level/Level;getGameRules()Lnet/minecraft/world/level/GameRules;")
    @Definition(id = "getBoolean", method = "Lnet/minecraft/world/level/GameRules;getBoolean(Lnet/minecraft/world/level/GameRules$Key;)Z")
    @Definition(id = "RULE_MOBGRIEFING", field = "Lnet/minecraft/world/level/GameRules;RULE_MOBGRIEFING:Lnet/minecraft/world/level/GameRules$Key;")
    @Expression("this.rabbit.level().getGameRules().getBoolean(RULE_MOBGRIEFING)")
    @WrapOperation(method = "canUse", at = @At("MIXINEXTRAS:EXPRESSION"))
    private boolean checkGGRule(GameRules instance, GameRules.Key<GameRules.BooleanValue> key, Operation<Boolean> original) {
        return GGDeferredChecker.getBoolean(instance, MobGriefingRules.CREATURES_EAT_PLANTS).orElse(original.call(instance, key));
    }
}
