package red.jackf.granulargamerules.mixins.mobgriefing.itempickup;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.entity.animal.allay.Allay;
import net.minecraft.world.level.GameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import red.jackf.granulargamerules.api.GGAPI;
import red.jackf.granulargamerules.impl.rules.MobGriefingRules;

@Mixin(Allay.class)
public class AllayMixin {
    @WrapOperation(method = "wantsToPickUp", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/GameRules;getBoolean(Lnet/minecraft/world/level/GameRules$Key;)Z"))
    private boolean checkGGRule(GameRules instance, GameRules.Key<GameRules.BooleanValue> key, Operation<Boolean> original) {
        return GGAPI.getBoolean(instance, MobGriefingRules.ITEMS_TAKEN_BY_ALLAYS);
    }
}
