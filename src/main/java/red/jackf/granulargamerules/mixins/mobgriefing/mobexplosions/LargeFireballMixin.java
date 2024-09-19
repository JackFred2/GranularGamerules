package red.jackf.granulargamerules.mixins.mobgriefing.mobexplosions;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Ghast;
import net.minecraft.world.entity.projectile.Fireball;
import net.minecraft.world.entity.projectile.LargeFireball;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import red.jackf.granulargamerules.impl.GGDeferredChecker;
import red.jackf.granulargamerules.impl.rules.MobGriefingRules;

@Mixin(LargeFireball.class)
public abstract class LargeFireballMixin extends Fireball {
    public LargeFireballMixin(EntityType<? extends Fireball> entityType, Level level) {
        super(entityType, level);
    }

    @Definition(id = "MOBSPAWNING", field = "Lnet/minecraft/world/level/GameRules;RULE_MOBGRIEFING:Lnet/minecraft/world/level/GameRules$Key;")
    @Definition(id = "getBoolean", method = "Lnet/minecraft/world/level/GameRules;getBoolean(Lnet/minecraft/world/level/GameRules$Key;)Z")
    @Definition(id = "getGameRules", method = "Lnet/minecraft/world/level/Level;getGameRules()Lnet/minecraft/world/level/GameRules;")
    @Definition(id = "level", method = "Lnet/minecraft/world/entity/projectile/LargeFireball;level()Lnet/minecraft/world/level/Level;")
    @Expression("this.level().getGameRules().getBoolean(MOBSPAWNING)")
    @WrapOperation(method = "onHit", at = @At("MIXINEXTRAS:EXPRESSION"))
    private boolean checkGGRule(GameRules instance, GameRules.Key<GameRules.BooleanValue> key, Operation<Boolean> original) {
        if (this.getOwner() instanceof Ghast) {
            return GGDeferredChecker.getBoolean(instance, MobGriefingRules.GHASTS_DESTROY_BLOCKS).orElse(original.call(instance, key));
        } else {
            return original.call(instance, key);
        }

    }
}
