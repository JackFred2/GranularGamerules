package red.jackf.granulargamerules.mixins.mobgriefing.itempickup;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.allay.Allay;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.level.GameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import red.jackf.granulargamerules.api.GGAPI;
import red.jackf.granulargamerules.impl.rules.MobGriefingRules;

@Mixin(Mob.class)
public abstract class MobMixin {
    @WrapOperation(method = "aiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/GameRules;getBoolean(Lnet/minecraft/world/level/GameRules$Key;)Z"))
    private boolean checkGGRule(GameRules instance, GameRules.Key<GameRules.BooleanValue> key, Operation<Boolean> original) {
        GameRules.Key<GameRules.BooleanValue> rule = switch ((Mob) (Object) this) {
            case Allay ignored: yield MobGriefingRules.ITEMS_TAKEN_BY_ALLAYS;
            case Piglin ignored: yield MobGriefingRules.ITEMS_TAKEN_BY_PIGLINS;
            case Villager ignored: yield MobGriefingRules.ITEMS_TAKEN_BY_VILLAGERS;
            default: yield MobGriefingRules.ITEMS_TAKEN_BY_OTHERS;
        };

        return GGAPI.getBoolean(instance, rule);
    }
}
