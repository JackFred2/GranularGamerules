package red.jackf.granulargamerules.mixins.universalanger;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.level.GameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import red.jackf.granulargamerules.impl.rules.UniversalAngerRules;

@Mixin(NeutralMob.class)
public interface NeutralMobMixin {
    @WrapOperation(method = "isAngryAtAllPlayers", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/GameRules;getBoolean(Lnet/minecraft/world/level/GameRules$Key;)Z"))
    private boolean checkGGRule(GameRules instance, GameRules.Key<GameRules.BooleanValue> parent, Operation<Boolean> original) {
        return UniversalAngerRules.testOverride(instance, this).orElse(original.call(instance, parent));
    }
}
