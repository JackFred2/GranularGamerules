package red.jackf.granulargamerules.mixins.mobgriefing.mobexplosions;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.projectile.LargeFireball;
import net.minecraft.world.entity.projectile.WitherSkull;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import red.jackf.granulargamerules.impl.GGDeferredChecker;
import red.jackf.granulargamerules.impl.rules.MobGriefingRules;

import java.util.Optional;

@Mixin(Level.class)
public class LevelMixin {
    @Definition(id = "MOBSPAWNING", field = "Lnet/minecraft/world/level/GameRules;RULE_MOBGRIEFING:Lnet/minecraft/world/level/GameRules$Key;")
    @Definition(id = "getBoolean", method = "Lnet/minecraft/world/level/GameRules;getBoolean(Lnet/minecraft/world/level/GameRules$Key;)Z")
    @Definition(id = "getGameRules", method = "Lnet/minecraft/world/level/Level;getGameRules()Lnet/minecraft/world/level/GameRules;")
    @Expression("this.getGameRules().getBoolean(MOBSPAWNING)")
    @WrapOperation(method = "explode(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/damagesource/DamageSource;Lnet/minecraft/world/level/ExplosionDamageCalculator;DDDFZLnet/minecraft/world/level/Level$ExplosionInteraction;ZLnet/minecraft/core/particles/ParticleOptions;Lnet/minecraft/core/particles/ParticleOptions;Lnet/minecraft/core/Holder;)Lnet/minecraft/world/level/Explosion;", at = @At("MIXINEXTRAS:EXPRESSION"))
    private boolean checkGGRule(GameRules instance, GameRules.Key<GameRules.BooleanValue> parent, Operation<Boolean> original, @Nullable Entity source) {
        var override = source != null ? switch (source) {
            case Creeper ignored: yield GGDeferredChecker.getBoolean(instance, MobGriefingRules.CREEPERS_DESTROY_BLOCKS);
            case LargeFireball ignored: yield GGDeferredChecker.getBoolean(instance, MobGriefingRules.GHASTS_DESTROY_BLOCKS);
            case WitherBoss ignored: yield GGDeferredChecker.getBoolean(instance, MobGriefingRules.WITHERS_DESTROY_BLOCKS);
            case WitherSkull ignored: yield GGDeferredChecker.getBoolean(instance, MobGriefingRules.WITHERS_DESTROY_BLOCKS);
            // todo other wither  and ghast abilities
            default: yield Optional.<Boolean>empty();
        } : Optional.<Boolean>empty();

        return override.orElse(original.call(instance, parent));
    }
}
