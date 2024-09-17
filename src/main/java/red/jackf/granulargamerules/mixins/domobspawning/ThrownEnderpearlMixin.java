package red.jackf.granulargamerules.mixins.domobspawning;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.entity.projectile.ThrownEnderpearl;
import net.minecraft.world.level.GameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import red.jackf.granulargamerules.impl.GGDeferredChecker;
import red.jackf.granulargamerules.impl.rules.MobSpawningRules;

@Mixin(ThrownEnderpearl.class)
public class ThrownEnderpearlMixin {
    @Definition(id = "MOBSPAWNING", field = "Lnet/minecraft/world/level/GameRules;RULE_DOMOBSPAWNING:Lnet/minecraft/world/level/GameRules$Key;")
    @Definition(id = "getBoolean", method = "Lnet/minecraft/world/level/GameRules;getBoolean(Lnet/minecraft/world/level/GameRules$Key;)Z")
    @Expression("?.getBoolean(MOBSPAWNING)")
    @WrapOperation(method = "onHit", at = @At("MIXINEXTRAS:EXPRESSION"))
    private boolean checkGGRule(GameRules instance, GameRules.Key<GameRules.BooleanValue> parent, Operation<Boolean> original) {
        return GGDeferredChecker.getBoolean(instance, MobSpawningRules.MONSTER).orElse(original.call(instance, parent));
    }
}
