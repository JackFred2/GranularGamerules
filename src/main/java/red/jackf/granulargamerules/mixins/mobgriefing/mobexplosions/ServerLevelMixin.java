package red.jackf.granulargamerules.mixins.mobgriefing.mobexplosions;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.projectile.LargeFireball;
import net.minecraft.world.entity.projectile.WitherSkull;
import net.minecraft.world.level.GameRules;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import red.jackf.granulargamerules.impl.GGDeferredChecker;
import red.jackf.granulargamerules.impl.rules.MobGriefingRules;

import java.util.Optional;

@Mixin(ServerLevel.class)
public class ServerLevelMixin {
    @Definition(id = "getGameRules", method = "Lnet/minecraft/server/level/ServerLevel;getGameRules()Lnet/minecraft/world/level/GameRules;")
    @Definition(id = "getBoolean", method = "Lnet/minecraft/world/level/GameRules;getBoolean(Lnet/minecraft/world/level/GameRules$Key;)Z")
    @Definition(id = "RULE_MOBGRIEFING", field = "Lnet/minecraft/world/level/GameRules;RULE_MOBGRIEFING:Lnet/minecraft/world/level/GameRules$Key;")
    @Expression("this.getGameRules().getBoolean(RULE_MOBGRIEFING)")
    @WrapOperation(method = "explode", at = @At("MIXINEXTRAS:EXPRESSION"))
    private boolean checkGGRule(GameRules instance, GameRules.Key<GameRules.BooleanValue> parent, Operation<Boolean> original, @Nullable Entity source) {
        var override = source != null ? switch (source) {
            case Creeper ignored: yield GGDeferredChecker.getBoolean(instance, MobGriefingRules.CREEPERS_DESTROY_BLOCKS);
            case LargeFireball ignored: yield GGDeferredChecker.getBoolean(instance, MobGriefingRules.GHASTS_DESTROY_BLOCKS);
            case WitherBoss ignored: yield GGDeferredChecker.getBoolean(instance, MobGriefingRules.WITHERS_DESTROY_BLOCKS);
            case WitherSkull ignored: yield GGDeferredChecker.getBoolean(instance, MobGriefingRules.WITHERS_DESTROY_BLOCKS);
            default: yield Optional.<Boolean>empty();
        } : Optional.<Boolean>empty();

        return override.orElse(original.call(instance, parent));
    }
}
