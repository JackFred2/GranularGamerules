package red.jackf.granulargamerules.mixins.domobspawning;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import red.jackf.granulargamerules.impl.rules.MobSpawningRules;

@Mixin(ServerChunkCache.class)
public abstract class ServerChunkCacheMixin {

    @Shadow @Final
    ServerLevel level;

    @Definition(id = "MOBSPAWNING", field = "Lnet/minecraft/world/level/GameRules;RULE_DOMOBSPAWNING:Lnet/minecraft/world/level/GameRules$Key;")
    @Definition(id = "getBoolean", method = "Lnet/minecraft/world/level/GameRules;getBoolean(Lnet/minecraft/world/level/GameRules$Key;)Z")
    @Expression("?.getBoolean(MOBSPAWNING)")
    @ModifyExpressionValue(method = "tickChunks", at = @At("MIXINEXTRAS:EXPRESSION"))
    private boolean doSpawningAnywayIfOverrideIsPresent(boolean original) {
        return original || MobSpawningRules.anySpawningOverrides(this.level);
    }
}
